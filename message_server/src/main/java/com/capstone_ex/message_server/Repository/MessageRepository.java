package com.capstone_ex.message_server.Repository;

import com.capstone_ex.message_server.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
}
