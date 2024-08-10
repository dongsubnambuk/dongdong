package com.capstone_ex.chat_server.DAO.User;

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
    public UserInfoEntity getUserById(String userId) {
        List<UserInfoEntity> users = userInfoRepository.findAllByUserId(userId);
        if (users.isEmpty()) {
            return null; // 사용자가 존재하지 않음
        } else if (users.size() > 1) {
            // 여러 사용자가 있을 경우, 첫 번째 사용자 반환 또는 다른 처리 로직 작성
            // 예: 가장 최근 사용자를 선택, 또는 예외를 던져 중복에 대한 오류 알림
            throw new IllegalArgumentException("Duplicate userId found.");
        } else {
            return users.get(0); // 단일 사용자 반환
        }
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
    public boolean existsById(String userId) {
        return userInfoRepository.existsByUserId(userId);
    }
}
