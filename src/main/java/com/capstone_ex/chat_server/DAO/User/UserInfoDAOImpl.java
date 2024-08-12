package com.capstone_ex.chat_server.DAO.User;

import com.capstone_ex.chat_server.DTO.ExternalDTO.ExternalUserInfoDTO;
import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInfoDAOImpl implements UserInfoDAO {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserInfoEntity getUserById(String uniqueId) {
        List<UserInfoEntity> users = userInfoRepository.findAllByUniqueId(uniqueId);
        if (users.isEmpty()) {
            return null;
        } else if (users.size() > 1) {
            throw new IllegalArgumentException("Duplicate Unique Id found.");
        } else {
            return users.get(0);
        }
    }

    @Override
    public void saveNewUser(ExternalUserInfoDTO externalUserInfoDTO) {
        UserInfoEntity user = UserInfoEntity.builder()
                .nickname(externalUserInfoDTO.getNickname())
                .uniqueId(externalUserInfoDTO.getUniqueId())
                .build();
        userInfoRepository.save(user);
    }

    @Override
    public List<UserInfoEntity> getAllUsers() {
        return userInfoRepository.findAll();
    }

    @Override
    public UserInfoEntity saveUser(UserInfoEntity user) {
        return userInfoRepository.save(user);
    }

    @Override
    public boolean existsById(String uniqueId) {
        return userInfoRepository.existsByUniqueId(uniqueId);
    }
}
