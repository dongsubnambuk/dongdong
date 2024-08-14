package com.capstone_ex.message_server.DTO;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private Long messageId;
    private String chatRoomId;
    private Boolean checkTime;
}
