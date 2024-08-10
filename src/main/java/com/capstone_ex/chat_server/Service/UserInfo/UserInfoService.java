package com.capstone_ex.chat_server.Service.UserInfo;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;

import java.util.List;

public interface UserInfoService {
    UserInfoEntity getUserById(String userId);
    UserInfoEntity saveUser(UserInfoEntity user);
    List<UserInfoEntity> getAllUsers();
    boolean existsById(String userId);
}
