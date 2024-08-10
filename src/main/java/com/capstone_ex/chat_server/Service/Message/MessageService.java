package com.capstone_ex.chat_server.Service.Message;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;

import java.util.List;

public interface MessageService {
    MessageEntity sendMessage(MessageEntity messageEntity);
    List<MessageEntity> getMessagesByChatRoomId(Long chatRoomId);
}
