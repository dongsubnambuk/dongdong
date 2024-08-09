package com.capstone_ex.chat_server.DAO.ChatRoom;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomDAO {
    ChatRoomEntity saveChatRoom(ChatRoomEntity chatRoomEntity);
    Optional<ChatRoomEntity> findChatRoomById(Long chatRoomId);
    List<ChatRoomEntity> findAllChatRooms();
    void deleteChatRoomById(Long chatRoomId);
    void addUserToChatRoom(UserInfoEntity userInfoEntity, ChatRoomEntity chatRoomEntity);
    void removeUserFromChatRoom(UserInfoEntity userInfoEntity, ChatRoomEntity chatRoomEntity);
}

