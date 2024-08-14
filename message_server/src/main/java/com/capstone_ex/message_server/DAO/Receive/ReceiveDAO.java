package com.capstone_ex.message_server.DAO.Receive;

import com.capstone_ex.message_server.DTO.ReceiveDTO;

public interface ReceiveDAO {
    void createNewMessage(ReceiveDTO receiveDTO);
}
