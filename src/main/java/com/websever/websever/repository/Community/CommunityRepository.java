package com.websever.websever.repository.Community;

import com.websever.websever.entity.community.postEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<postEntity, Integer> {
}
