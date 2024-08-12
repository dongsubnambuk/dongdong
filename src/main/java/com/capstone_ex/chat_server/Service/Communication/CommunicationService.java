package com.capstone_ex.chat_server.Service.Communication;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;

public interface CommunicationService {

    ExternalUserInfoDTO getUserInfo(String uniqueId);
}
