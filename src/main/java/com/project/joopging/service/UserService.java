package com.project.joopging.service;

import com.project.joopging.dto.user.*;

import com.project.joopging.enums.UserRoleEnum;
import com.project.joopging.exception.CustomErrorException;
import com.project.joopging.model.User;
import com.project.joopging.repository.UserRepository;
import com.project.joopging.security.JwtTokenProvider;
import com.project.joopging.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final signupValidator signupValidator;
    private final PasswordEncoder passwordEncoder;

    public User userFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) userDetails).getUser();
        } else {
            log.error("로그인이 필요합니다");
            throw new CustomErrorException("로그인이 필요합니다.");
        }
    }

    public void userCheckEnumRole(User user) {
        if (user.getRole() == UserRoleEnum.USER) {
            throw new CustomErrorException("관리자 권한이 없습니다.");
        }
    }

    //회원가입
    public boolean registerUser(SignupRequestDto requestDto) {
        User user = signupValidator.validate(requestDto);
        userRepository.save(user);
        return true;
    }

    //로그인
    public User login(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail()).orElseThrow(
                () -> new CustomErrorException("이메일을 찾을 수 없습니다")
        );
        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new CustomErrorException("비밀번호가 일치하지 않습니다");
        }
        return user;
    }

    //회원삭제
    public boolean deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomErrorException("이메일을 찾을 수 없습니다")
        );
        userRepository.delete(user);
        return true;
    }

    //회원 정보 수정
    public EditUserResponseDto editUserInfo(EditUserRequestDto editUserInfoDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        String userImg = editUserInfoDto.getUserImg();
        String intro = editUserInfoDto.getIntro();

        String password;
        if (user.getPassword().equals(editUserInfoDto.getPassword())) {
            password = user.getPassword();
        } else {
            password = editUserInfoDto.getPassword();
        }

        String distance = editUserInfoDto.getDistance();

        String location = editUserInfoDto.getLocation();

        String type = editUserInfoDto.getType();

        user.setUserImg(userImg);
        user.setDistance(distance);
        user.setPassword(passwordEncoder.encode(password));
        user.setLocation(location);
        user.setType(type);
        user.setIntro(intro);

        userRepository.save(user);

        return new EditUserResponseDto(user);
    }

    public LoginDetailReponseDto toSetLoginDetailResponse(User user) {

        return user.toBuildDetailUser();
    }

    public MainPageResponseUserDto getUserInfo(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomErrorException("없는 사용자 입니다")
        );

        return new MainPageResponseUserDto(user);
    }

    public MyBadgeListResponseDto getMyBadgeListByUser(User user) {

        return MyBadgeListResponseDto.builder().build();
    }

    //닉네임 중복 검사
    public void nicknameCheck(String nickname) {
        Optional<User> nicknameFound = userRepository.findByNickname(nickname);

        if (nicknameFound.isPresent()) {
            throw new CustomErrorException("중복된 닉네임 입니다 ");
        }
    }

    //이메일 중복 검사
    public void emailCheck(String email) {
        Optional<User> emailFound = userRepository.findByEmail(email);

        if (emailFound.isPresent()) {
            throw new CustomErrorException("중복된 이메일 입니다 ");
        }
    }
}
