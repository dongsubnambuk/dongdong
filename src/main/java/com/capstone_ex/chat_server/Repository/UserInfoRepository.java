package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {

    // 이메일로 사용자 정보를 조회하는 메소드
    Optional<UserInfoEntity> findByEmail(String email);

    // 닉네임으로 사용자 정보를 조회하는 메소드 (필요시 추가)
    Optional<UserInfoEntity> findByNickname(String nickname);

    // 특정 ID의 사용자가 존재하는지 확인하는 메소드 (필요시 추가)
    boolean existsByUserId(String userId);

    // 특정 이메일의 사용자가 존재하는지 확인하는 메소드 (필요시 추가)
    boolean existsByEmail(String email);
}
