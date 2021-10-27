package com.project.joopging.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReviewRequestDto {
    String content;
    String reviewImg;
    int star;
}
