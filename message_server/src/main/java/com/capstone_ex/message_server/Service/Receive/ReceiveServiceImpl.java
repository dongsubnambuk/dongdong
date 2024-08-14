package com.capstone_ex.message_server.Service.Receive;

import com.capstone_ex.message_server.DAO.Receive.ReceiveDAO;
import com.capstone_ex.message_server.DTO.ReceiveDTO;
import com.capstone_ex.message_server.Repository.UserRepository;
import org.springframework.stereotype.Service;
import com.capstone_ex.message_server.Entity.UserEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReceiveServiceImpl implements ReceiveService{
    private final ReceiveDAO receiveDAO;
    private final UserRepository userRepository;

    public ReceiveServiceImpl(ReceiveDAO receiveDAO, UserRepository userRepository) {
        this.receiveDAO = receiveDAO;
        this.userRepository = userRepository;
    }

    @Override
    public void processReceivedMessage(ReceiveDTO receiveDTO) {
        receiveDAO.createNewMessage(receiveDTO);
        // 메시지를 데이터베이스에 저장
    }

    @Override
    public Set<String> getUserIdsInChatRoom(Long chatRoomId) {
        return userRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeUserFromChatRoom(Long chatRoomId, String uniqueId) {
        UserEntity userEntity = userRepository.findByUserIdAndChatRoomId(uniqueId, chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("User not found in chat room"));

        userRepository.delete(userEntity);
    }
}
