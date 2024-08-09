package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
}