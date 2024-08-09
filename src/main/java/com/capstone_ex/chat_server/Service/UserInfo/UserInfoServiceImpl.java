package com.capstone_ex.chat_server.Service.UserInfo;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import com.capstone_ex.chat_server.Repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;

    @Override
    public UserInfoEntity getUserByEmail(String email) {
        return userInfoRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
    }

    @Override
    public List<UserInfoEntity> getAllUsers() {
        return userInfoRepository.findAll();
    }
}
