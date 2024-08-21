package com.capstone_ex.chat_server.Service.Communication;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;

public interface CommunicationService {
    ExternalUserInfoDTO getUserInfo(String uniqueId);
    void callUpdateChatInfo(Long chatRoomId, String userId);
    void callRemoveChatRoom(Long chatRoomId);
    void callDeleteChatUser(Long chatRoomId, String uniqueId);
}
