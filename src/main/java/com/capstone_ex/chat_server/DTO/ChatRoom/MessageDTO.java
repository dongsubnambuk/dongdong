package com.capstone_ex.chat_server.DTO.ChatRoom;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageDTO {
    private Long messageId;
    private String userId;
    private String content;
    private LocalDateTime sendTime = LocalDateTime.now();
    private boolean check = false;
}