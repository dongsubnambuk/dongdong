package com.capstone_ex.message_server.DAO.Send;

import com.capstone_ex.message_server.DTO.SendDTO;

import java.util.List;
import java.util.Set;

public interface SendDAO {
    List<SendDTO> getMessagesByChatRoomId(Long chatRoomId);
    Set<String> getUserIdsByChatRoomId(Long chatRoomId);
}
