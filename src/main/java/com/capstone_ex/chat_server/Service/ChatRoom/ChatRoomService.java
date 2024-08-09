package com.capstone_ex.chat_server.Service.ChatRoom;

import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.Entity.UserInfoEntity;

import java.util.List;

public interface ChatRoomService {
    ChatRoomDTO createChatRoom(String chatName, String description, String createdBy);
    ChatRoomDTO getChatRoomById(Long chatRoomId);
    List<ChatRoomDTO> getAllChatRooms();
    void deleteChatRoom(Long chatRoomId);
    void addUserToChatRoom(UserInfoEntity userInfoEntity, Long chatRoomId);
    void removeUserFromChatRoom(UserInfoEntity userInfoEntity, Long chatRoomId);

}
