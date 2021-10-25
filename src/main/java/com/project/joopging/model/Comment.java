package com.project.joopging.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    String content;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    User userComment;


    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    Post postComment;

    public Comment(String content, User userComment, Post postComment) {
        this.content = content;
        this.userComment = userComment;
        this.postComment = postComment;
    }
}
