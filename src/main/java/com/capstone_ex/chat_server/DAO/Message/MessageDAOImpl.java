package com.capstone_ex.chat_server.DAO.Message;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;
import com.capstone_ex.chat_server.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageDAOImpl implements MessageDAO {

    private final MessageRepository messageRepository;

    @Override
    public MessageEntity saveMessage(MessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }

    @Override
    public List<MessageEntity> getMessagesByChatRoomId(Long chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }
}
