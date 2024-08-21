package com.capstone_ex.message_server.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class checkDTO {
    private Long messageId;
    private Long chatRoomId;
}
