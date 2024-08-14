package com.capstone_ex.message_server.DAO.Send;

import com.capstone_ex.message_server.DTO.SendDTO;
import com.capstone_ex.message_server.Entity.MessageEntity;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.MessageRepository;
import com.capstone_ex.message_server.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SendDAOImpl implements SendDAO{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public List<SendDTO> getMessagesByChatRoomId(Long chatRoomId) {
        List<MessageEntity> messages = messageRepository.findByChatRoomIdOrderBySendTimeDesc(chatRoomId);

        // 역순으로 정렬
        Collections.reverse(messages);

        return messages.stream()
                .map(message -> SendDTO.builder()
                        .userId(message.getUserId())
                        .messageContent(message.getMessageContent())
                        .chatRoomId(message.getChatRoomId())
                        .sendTime(message.getSendTime())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getUserIdsByChatRoomId(Long chatRoomId) {
        return userRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toSet());
    }
}
