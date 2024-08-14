package com.capstone_ex.message_server.DAO.Receive;

import com.capstone_ex.message_server.DTO.ReceiveDTO;
import com.capstone_ex.message_server.Entity.MessageEntity;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.MessageRepository;
import com.capstone_ex.message_server.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashSet;

@Repository
@RequiredArgsConstructor
public class ReceiveDAOImpl implements ReceiveDAO{
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    @Override
    public void createNewMessage(ReceiveDTO receiveDTO) {
        // MessageEntity 생성 및 저장
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(receiveDTO.getUserId())
                .messageContent(receiveDTO.getMessageContent())
                .chatRoomId(receiveDTO.getChatRoomId())
                .checkCount(1L)
                .sendTime(LocalDateTime.now())
                .build();

        // 메시지 저장
        messageRepository.save(messageEntity);

        // UserEntity가 이미 존재하는지 확인하고 없으면 생성
        UserEntity userEntity = userRepository.findByUserIdAndChatRoomId(receiveDTO.getUserId(), receiveDTO.getChatRoomId())
                .orElseGet(() -> {
                    UserEntity newUserEntity = UserEntity.builder()
                            .userId(receiveDTO.getUserId())
                            .chatRoomId(receiveDTO.getChatRoomId())
                            .messageId(messageEntity.getId()) // 저장된 메시지의 ID를 설정
                            .messages(new HashSet<>())
                            .build();
                    return userRepository.save(newUserEntity);
                });

        // 존재하던 엔티티면 메시지 ID 업데이트
        if (userEntity.getId() != null) {
            userEntity.setMessageId(messageEntity.getId());
            userRepository.save(userEntity);
        }

        // 다대다 관계에 사용자 추가
        userEntity.getMessages().add(messageEntity);

        // 최종적으로 메시지 정보 저장
        messageRepository.save(messageEntity);
    }
}
