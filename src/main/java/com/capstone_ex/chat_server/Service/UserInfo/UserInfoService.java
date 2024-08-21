package com.capstone_ex.chat_server.Service.UserInfo;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;

import java.util.List;

public interface UserInfoService {
    UserInfoEntity getUserByUniqueId(String userId);
    UserInfoEntity saveUser(UserInfoEntity user);
    List<UserInfoEntity> getAllUsers();
    boolean existsById(String userId);
    void saveNewUser(ExternalUserInfoDTO externalUserInfoDTO);
}
