package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List; // <- 추가 필요

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
//    Optional<UserInfoEntity> findByUserId(String userId); // 여전히 Optional로 처리 (기존 코드)
    boolean existsByUniqueId(String userId);

    Optional<UserInfoEntity> findByUniqueId(String uniqueId);

    List<UserInfoEntity> findAllByUniqueId(String uniqueId);

}

