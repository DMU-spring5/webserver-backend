package com.websever.websever.service.Community;

import com.websever.websever.dto.request.CommunityRequest;
import com.websever.websever.dto.response.CommunityResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.community.postEntity;
import com.websever.websever.repository.Community.CommunityRepository;
import com.websever.websever.repository.auth.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public CommunityResponse create(String currentUserId, CommunityRequest request) {
        UserEntity userEntity = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("작성 사용자 정보를 찾을 수 없습니다. (ID: " + currentUserId + ")"));

        postEntity newPost = new postEntity();
        newPost.setTitle(request.getTitle());
        newPost.setContent(request.getContent());

        newPost.setUsers_id(userEntity);
        postEntity savePost = communityRepository.save(newPost); // DB 저장 후 ID가 채워진 객체

        return CommunityResponse.create(savePost);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getAllPosts() {
        List<postEntity> posts = communityRepository.findAll();

        return posts.stream()
                .map(CommunityResponse::create)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public CommunityResponse getPostById(Integer postId) {
        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        return CommunityResponse.create(post);
    }

    @Transactional
    public CommunityResponse updatePost(Integer postId, String currentUserId, CommunityRequest request) {
        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getUsers_id().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return CommunityResponse.create(post);
    }

    @Transactional
    public void deletePost(Integer postId, String currentUserId) {
        postEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        // 💡 권한 검증
        if (!post.getUsers_id().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        communityRepository.delete(post);
    }
}
