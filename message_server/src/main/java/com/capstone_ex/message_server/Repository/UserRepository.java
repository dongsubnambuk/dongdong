package com.capstone_ex.message_server.Repository;

import com.capstone_ex.message_server.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserIdAndChatRoomId(String userId, Long chatRoomId);
    List<UserEntity> findByChatRoomId(Long chatRoomId);

}
