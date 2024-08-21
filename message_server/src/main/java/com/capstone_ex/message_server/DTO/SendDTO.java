package com.capstone_ex.message_server.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendDTO {
    private String userId;
    private Long messageId;
    private String messageContent;
    private Long chatRoomId;
    private LocalDateTime sendTime;
}
