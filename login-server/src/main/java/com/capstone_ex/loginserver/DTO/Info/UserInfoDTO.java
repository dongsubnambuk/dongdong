package com.capstone_ex.loginserver.DTO.Info;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserInfoDTO {
    private String nickname;
    private String uniqueId;
}
