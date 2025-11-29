package com.websever.websever.service.Community;

import com.websever.websever.dto.request.CommentRequest;
import com.websever.websever.dto.response.CommentResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.community.CommentEntity;
import com.websever.websever.entity.community.postEntity;
import com.websever.websever.repository.Community.CommentRepository;
import com.websever.websever.repository.Community.CommunityRepository;
import com.websever.websever.repository.auth.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public CommentResponse create(String currentUserId, Integer postId, CommentRequest request) {
        UserEntity userEntity = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("작성 사용자 정보를 찾을 수 없습니다. (ID: " + currentUserId + ")"));

        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        CommentEntity newComment = new CommentEntity();
        newComment.setContent(request.getContent());
        newComment.setUsersId(userEntity);
        newComment.setPostId(post);

        CommentEntity saveComment = commentRepository.save(newComment);

        return CommentResponse.create(saveComment);
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Integer postId, Integer commentId) {
        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        CommentEntity comment = commentRepository.findByCommentIdAndPostId(commentId, post)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 게시글(ID: " + postId + ")에서 댓글(ID: " + commentId + ")을 찾을 수 없습니다."));

        return CommentResponse.create(comment);
    }

    @Transactional
    public void deleteComment(Integer commentId, String currentUserId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!comment.getUsersId().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}