package com.capstone_ex.chat_server.Service.UserInfo;

import com.capstone_ex.chat_server.DAO.User.UserInfoDAO;
import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoDAO userInfoDAO;

    @Override
    public UserInfoEntity getUserByUniqueId(String uniqueId) {
        return userInfoDAO.getUserById(uniqueId);
    }

    @Override
    public List<UserInfoEntity> getAllUsers() {
        return userInfoDAO.getAllUsers();
    }

    @Override
    public UserInfoEntity saveUser(UserInfoEntity user) {
        return userInfoDAO.saveUser(user);
    }

    @Override
    public void saveNewUser(ExternalUserInfoDTO externalUserInfoDTO){
        userInfoDAO.saveNewUser(externalUserInfoDTO);
    }

    @Override
    public boolean existsById(String userId) {
        return userInfoDAO.existsById(userId);
    }
}
