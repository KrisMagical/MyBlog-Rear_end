package com.example.demo.repository;

import com.example.demo.model.LikeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeLogRepository extends JpaRepository<LikeLog, Long> {
    int countByPostIdAndPositive(Long postId,boolean positive);
    boolean existsByPostIdAndIdentifier(Long postId, String identifier);
    LikeLog findByPostIdAndIdentifier(Long postId, String identifier);
}
