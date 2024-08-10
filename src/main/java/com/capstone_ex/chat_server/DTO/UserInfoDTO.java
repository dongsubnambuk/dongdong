package com.capstone_ex.chat_server.DTO;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String userId;
    private String nickname;
    private String email;

    public UserInfoDTO(UserInfoEntity userInfoEntity) {
        this.userId = userInfoEntity.getUserId();
        this.nickname = userInfoEntity.getNickname();
        this.email = userInfoEntity.getEmail();
    }
}
