package com.capstone_ex.chat_server.Service.Message;

import com.capstone_ex.chat_server.DAO.Message.MessageDAO;
import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageDAO messageDAO;

    @Override
    public MessageEntity sendMessage(MessageEntity messageEntity) {
        return messageDAO.saveMessage(messageEntity);
    }

    @Override
    public List<MessageEntity> getMessagesByChatRoomId(Long chatRoomId) {
        return messageDAO.getMessagesByChatRoomId(chatRoomId);
    }
}
