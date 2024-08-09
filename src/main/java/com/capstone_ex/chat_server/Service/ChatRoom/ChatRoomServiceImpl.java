package com.capstone_ex.chat_server.Service;

import com.capstone_ex.chat_server.DAO.ChatRoom.ChatRoomDAO;
import com.capstone_ex.chat_server.DTO.ChatRoom.ChatRoomDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import com.capstone_ex.chat_server.Service.ChatRoom.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomDAO chatRoomDAO;

    @Override
    public ChatRoomDTO createChatRoom(String chatName, String description, String createdBy) {
        ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .chatName(chatName)
                .description(description)
                .build();
        ChatRoomEntity savedChatRoom = chatRoomDAO.saveChatRoom(chatRoomEntity);
        return convertEntityToDTO(savedChatRoom);
    }

    @Override
    public ChatRoomDTO getChatRoomById(Long chatRoomId) {
        return chatRoomDAO.findChatRoomById(chatRoomId)
                .map(this::convertEntityToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    @Override
    public List<ChatRoomDTO> getAllChatRooms() {
        return chatRoomDAO.findAllChatRooms().stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteChatRoom(Long chatRoomId) {
        chatRoomDAO.deleteChatRoomById(chatRoomId);
    }

    @Override
    public void addUserToChatRoom(UserInfoEntity userInfoEntity, Long chatRoomId) {
        ChatRoomEntity chatRoomEntity = chatRoomDAO.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoomDAO.addUserToChatRoom(userInfoEntity, chatRoomEntity);
    }

    @Override
    public void removeUserFromChatRoom(UserInfoEntity userInfoEntity, Long chatRoomId) {
        ChatRoomEntity chatRoomEntity = chatRoomDAO.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
        chatRoomDAO.removeUserFromChatRoom(userInfoEntity, chatRoomEntity);
    }

    private ChatRoomDTO convertEntityToDTO(ChatRoomEntity chatRoomEntity) {
        return ChatRoomDTO.builder()
                .chatRoomId(chatRoomEntity.getId())
                .chatName(chatRoomEntity.getChatName())
                .description(chatRoomEntity.getDescription())
                .build();
    }
}
