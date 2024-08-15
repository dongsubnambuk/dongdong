package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    List<ChatRoomEntity> findByUsersContaining(UserInfoEntity user);
}
