package com.capstone_ex.chat_server.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserInfoDTO {
    private Long userId;
    private String email;
    private String nickname;
}
