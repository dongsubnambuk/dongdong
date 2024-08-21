package com.capstone_ex.chat_server.DTO.ExternalDTO;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserInfoDTO {
    private String nickname;
    private String uniqueId;
}
