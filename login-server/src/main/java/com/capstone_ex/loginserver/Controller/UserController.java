package com.capstone_ex.loginserver.Controller;

import com.capstone_ex.loginserver.Entity.UserEntity;
import com.capstone_ex.loginserver.Service.UserService;
import com.capstone_ex.loginserver.Service.CustomUserDetailsService;
import com.capstone_ex.loginserver.Security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.6:3000"})
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserEntity user) {
        UserEntity savedUser = userService.registerUser(user.getEmail(), user.getNickname(), user.getPassword());
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserEntity user) throws AuthenticationException {
        // 사용자 인증
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        // 사용자 정보 로드
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails.getUsername());

        // 사용자 정보 가져오기
        UserEntity loggedInUser = userService.getUserByEmail(user.getEmail());

        // 응답에 토큰과 사용자 정보 포함
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", loggedInUser);  // 사용자 정보를 'user' 키로 포함

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping
    public ResponseEntity<String> apiRoot() {
        return ResponseEntity.ok("success");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

}
