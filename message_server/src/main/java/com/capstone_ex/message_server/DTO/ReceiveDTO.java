package com.capstone_ex.message_server.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveDTO {
    private String userId;
    private String messageContent;
    private Long chatRoomId;
}
