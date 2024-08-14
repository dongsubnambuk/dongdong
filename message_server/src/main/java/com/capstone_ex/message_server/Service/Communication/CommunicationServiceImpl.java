package com.capstone_ex.message_server.Service.Communication;

import com.capstone_ex.message_server.Entity.UserEntity;
import com.capstone_ex.message_server.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunicationServiceImpl implements CommunicationService {

    private final UserRepository userRepository;

    public CommunicationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUsersToChatRoom(Long chatRoomId, String uniqueId) {
        // 기존에 동일한 userId와 chatRoomId로 저장된 엔티티가 있는지 확인
        Optional<UserEntity> existingUser = userRepository.findByUserIdAndChatRoomId(uniqueId, chatRoomId);
        if (existingUser.isEmpty()) {
            // 존재하지 않으면 새로운 엔티티를 생성하고 저장
            UserEntity newUserEntity = UserEntity.builder()
                    .userId(uniqueId)
                    .chatRoomId(chatRoomId)
                    .messageId(0L)  // 기본 메시지 ID를 null로 설정 (필요 시 업데이트)
                    .build();
            userRepository.save(newUserEntity);
        }
    }
}
