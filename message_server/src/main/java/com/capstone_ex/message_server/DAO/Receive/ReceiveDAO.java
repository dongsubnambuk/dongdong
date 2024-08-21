package com.capstone_ex.message_server.DAO.Receive;

import com.capstone_ex.message_server.DTO.MessageDTO;
import com.capstone_ex.message_server.DTO.ReceiveDTO;

public interface ReceiveDAO {
    MessageDTO createNewMessage(ReceiveDTO receiveDTO);
}
