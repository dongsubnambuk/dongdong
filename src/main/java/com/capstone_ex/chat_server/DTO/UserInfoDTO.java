package com.capstone_ex.chat_server.DTO;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String nickname;
    private String uniqueId;

    public UserInfoDTO(UserInfoEntity userInfoEntity) {
        this.nickname = userInfoEntity.getNickname();
        this.uniqueId = userInfoEntity.getUniqueId();
    }
}
