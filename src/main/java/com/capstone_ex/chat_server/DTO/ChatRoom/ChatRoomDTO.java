package com.capstone_ex.chat_server.DTO.ChatRoom;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String creatorId;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 이거 null이 아닐 때만 직렬화 되도록 바꿔놓음
    private String selectedId; // 추가된 필드

    public ChatRoomDTO(ChatRoomEntity chatRoomEntity) {
        this.chatRoomId = chatRoomEntity.getId();
        this.chatName = chatRoomEntity.getChatName();
        this.creatorId = chatRoomEntity.getCreatorId(); // 생성자의 ID를 저장
    }

//    public ChatRoomDTO(ChatRoomEntity chatRoomEntity, String selectedId) {
//        this.chatRoomId = chatRoomEntity.getId();
//        this.chatName = chatRoomEntity.getChatName();
//        this.creatorId = chatRoomEntity.getCreatorId();
//        this.selectedId = selectedId;
//    }
}
