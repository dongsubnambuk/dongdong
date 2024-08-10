package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List; // <- 추가 필요

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
    Optional<UserInfoEntity> findByUserId(String userId); // 여전히 Optional로 처리 (기존 코드)
    boolean existsByUserId(String userId);

    // 여러 개의 유저를 반환할 경우 이 메서드를 사용
    List<UserInfoEntity> findAllByUserId(String userId);
}

