package com.capstone_ex.chat_server.Service.UserInfo;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;

import java.util.List;

public interface UserInfoService {
    UserInfoEntity getUserByEmail(String email);
    List<UserInfoEntity> getAllUsers();
}
