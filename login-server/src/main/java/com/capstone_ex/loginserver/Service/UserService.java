package com.capstone_ex.loginserver.Service;

import com.capstone_ex.loginserver.Entity.UserEntity;
import com.capstone_ex.loginserver.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(String email, String nickname, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        UserEntity user = UserEntity.builder()
                .email(email)
                .nickname(nickname)
                .uniqueId(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

}
