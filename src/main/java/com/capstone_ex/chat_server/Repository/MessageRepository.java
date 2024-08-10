package com.capstone_ex.chat_server.Repository;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByChatRoomId(Long chatRoomId);
}

