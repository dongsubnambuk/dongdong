package com.capstone_ex.chat_server.DAO.User;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;

import java.util.List;

public interface UserInfoDAO {
    UserInfoEntity getUserById(String userId);
    UserInfoEntity saveUser(UserInfoEntity user);  // 새로운 메소드 추가
    boolean existsById(String userId);  // 존재 여부 확인 메소드 추가
    List<UserInfoEntity> getAllUsers(); // 이 메서드를 추가합니다.
}
