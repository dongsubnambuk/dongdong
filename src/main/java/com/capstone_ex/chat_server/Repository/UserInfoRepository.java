package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfoEntity, Long> {
}
