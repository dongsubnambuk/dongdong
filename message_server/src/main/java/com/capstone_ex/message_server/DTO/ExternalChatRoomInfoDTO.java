package com.capstone_ex.message_server.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalChatRoomInfoDTO {
    private Long chatRoomId;
    private String uniqueId;
}
