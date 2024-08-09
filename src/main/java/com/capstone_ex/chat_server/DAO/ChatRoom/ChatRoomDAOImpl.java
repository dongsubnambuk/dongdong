package com.capstone_ex.chat_server.DAO.ChatRoom;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.ChatRoomRepository;
import com.capstone_ex.chat_server.Repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomDAOImpl implements ChatRoomDAO {

    private final ChatRoomRepository chatRoomRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public ChatRoomEntity saveChatRoom(ChatRoomEntity chatRoomEntity) {
        return chatRoomRepository.save(chatRoomEntity);
    }

    @Override
    public Optional<ChatRoomEntity> findChatRoomById(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId);
    }

    @Override
    public List<ChatRoomEntity> findAllChatRooms() {
        return chatRoomRepository.findAll();
    }

    @Override
    public void deleteChatRoomById(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

    @Override
    public void addUserToChatRoom(UserInfoEntity userInfoEntity, ChatRoomEntity chatRoomEntity) {
        chatRoomEntity.getUsers().add(userInfoEntity);
        chatRoomRepository.save(chatRoomEntity);
    }

    @Override
    public void removeUserFromChatRoom(UserInfoEntity userInfoEntity, ChatRoomEntity chatRoomEntity) {
        chatRoomEntity.getUsers().remove(userInfoEntity);
        chatRoomRepository.save(chatRoomEntity);
    }
}
