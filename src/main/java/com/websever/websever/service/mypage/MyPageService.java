package com.websever.websever.service.mypage;

import com.websever.websever.dto.request.ChangePasswordRequest;
import com.websever.websever.dto.response.MyCommentDetailResponse;
import com.websever.websever.dto.response.MyCommentResponse;
import com.websever.websever.dto.response.MyLikedPostDetailResponse;
import com.websever.websever.dto.response.MyLikedPostResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.community.CommentEntity;
import com.websever.websever.entity.community.LikeEntity;
import com.websever.websever.entity.community.postEntity;
import com.websever.websever.repository.Community.CommentRepository;
import com.websever.websever.repository.Community.CommunityRepository;
import com.websever.websever.repository.Community.LikeRepository;
import com.websever.websever.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final LikeRepository likeRepository;
    private final CommunityRepository communityRepository;

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 기존 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 2. 새 비밀번호 유효성 검사 (필요 시 추가 로직 구현)
        // 예: if (request.getNewPassword().length() < 8) ...

        // 3. 새 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // @Transactional 안에서는 save 호출 없이도 더티 체킹으로 업데이트되지만, 명시적으로 호출해도 무방합니다.
    }

    /**
     * 내가 쓴 댓글(과 게시글) 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MyCommentResponse> getMyComments(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 댓글 조회
        List<CommentEntity> comments = commentRepository.findByUsersIdOrderByCreatedAtDesc(user);

        // DTO 변환
        return comments.stream()
                .map(MyCommentResponse::from)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public MyCommentDetailResponse getMyCommentDetail(String userId, Integer commentId) {
        // 1. 댓글 조회
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다. ID: " + commentId));

        // 2. 권한 검증 (내 댓글인지 확인)
        if (!comment.getUsersId().getUserId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 조회할 수 있습니다.");
        }

        return MyCommentDetailResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public List<MyLikedPostResponse> getMyLikedPosts(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 유저가 좋아요 누른 내역 조회
        List<LikeEntity> likes = likeRepository.findByUserOrderByCreatedAtDesc(user);

        // 2. DTO로 변환
        return likes.stream()
                .map(like -> {
                    postEntity post = like.getPost();
                    // 해당 게시글의 총 좋아요 수 조회
                    Integer count = likeRepository.countByPost(post);
                    return MyLikedPostResponse.of(post, count);
                })
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public MyLikedPostDetailResponse getMyLikedPostDetail(String userId, Integer postId) {
        // 1. 사용자 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 게시글 조회
        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. ID: " + postId));

        // 4. 해당 게시글의 총 좋아요 수 계산
        Integer likeCount = likeRepository.countByPost(post);

        return MyLikedPostDetailResponse.of(post, likeCount);
    }
}