package com.capstone_ex.message_server.DAO.Receive;

import com.capstone_ex.message_server.DTO.MessageDTO;
import com.capstone_ex.message_server.DTO.ReceiveDTO;
import com.capstone_ex.message_server.Entity.MessageEntity;
import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.MessageRepository;
import com.capstone_ex.message_server.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReceiveDAOImpl implements ReceiveDAO{
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    @Override
    public MessageDTO createNewMessage(ReceiveDTO receiveDTO) {
        // 사용자와 채팅방 ID로 UserEntity를 찾음
        Optional<UserEntity> optionalUserEntity = userRepository.findByUserIdAndChatRoomId(receiveDTO.getUserId(), receiveDTO.getChatRoomId());

        // 유저가 존재하지 않으면 예외 발생
        if (optionalUserEntity.isEmpty()) {
            throw new IllegalArgumentException("해당 유저와 채팅방 조합이 존재하지 않습니다.");
        }

        // 유저가 존재하는 경우
        UserEntity userEntity = optionalUserEntity.get();

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

        // 존재하던 엔티티면 메시지 ID 업데이트
        userEntity.setMessageId(messageEntity.getId());
        userEntity.getMessages().add(messageEntity);
        userRepository.save(userEntity);

        // 메시지 정보 DTO로 반환
        return MessageDTO.builder()
                .messageId(messageEntity.getId())
                .chatRoomId(messageEntity.getChatRoomId())
                .sendTime(messageEntity.getSendTime())
                .build();
    }

}
