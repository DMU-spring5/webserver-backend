package com.websever.websever.repository.Community;

import com.websever.websever.entity.community.CommentEntity;
import com.websever.websever.entity.community.postEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Optional<CommentEntity> findByCommentIdAndPostId(Integer commentId, postEntity postId);

}
