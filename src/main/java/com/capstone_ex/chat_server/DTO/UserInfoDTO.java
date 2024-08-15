package com.capstone_ex.chat_server.DTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String nickname;
    private String uniqueId;

    public UserInfoDTO(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
