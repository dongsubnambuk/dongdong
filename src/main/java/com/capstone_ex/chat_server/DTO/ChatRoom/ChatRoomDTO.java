package com.capstone_ex.chat_server.DTO.ChatRoom;

import com.capstone_ex.chat_server.DTO.UserInfoDTO;
import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long chatRoomId;
    private String chatName;
    private String description;
    private String creatorId;
    private UserInfoDTO creatorInfo; // 추가된 필드

    public ChatRoomDTO(ChatRoomEntity chatRoomEntity) {
        this.chatRoomId = chatRoomEntity.getId();
        this.chatName = chatRoomEntity.getChatName();
        this.description = chatRoomEntity.getDescription();
        this.creatorId = chatRoomEntity.getCreator().getUserId(); // 채팅방 생성자 ID
        this.creatorInfo = new UserInfoDTO(chatRoomEntity.getCreator()); // 채팅방 생성자 정보
    }
}
