package com.websever.websever.entity.community;

import com.websever.websever.entity.auth.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(nullable = false, length = 255)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @JoinColumn(name = "post_title")
    private String postTitle;

    @JoinColumn(name = "post_content")
    private String postContent;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity usersId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private postEntity postId;
}
