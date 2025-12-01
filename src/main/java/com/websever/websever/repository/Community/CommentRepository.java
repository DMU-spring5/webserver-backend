package com.websever.websever.repository.Community;

import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.community.CommentEntity;
import com.websever.websever.entity.community.postEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Optional<CommentEntity> findByCommentIdAndPostId(Integer commentId, postEntity postId);

    List<CommentEntity> findByUsersIdOrderByCreatedAtDesc(UserEntity userId);
}
