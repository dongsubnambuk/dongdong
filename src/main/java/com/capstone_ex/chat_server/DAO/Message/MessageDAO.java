package com.capstone_ex.chat_server.DAO.Message;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;

import java.util.List;

public interface MessageDAO {
    MessageEntity saveMessage(MessageEntity messageEntity);
    List<MessageEntity> getMessagesByChatRoomId(Long chatRoomId);
}
