package com.capstone_ex.chat_server.DTO.ChatRoom;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatRoomDTO {
    private Long chatRoomId;
    private String chatName;
    private String description;
}
