package com.capstone_ex.chat_server.Controller;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Service.UserInfo.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat/user")
@CrossOrigin(origins = {"http://localhost:3000", "http://192.168.0.6:3000"})
@RequiredArgsConstructor
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/{uniqueId}")
    public ResponseEntity<UserInfoEntity> getUserByUniqueId(@PathVariable String uniqueId) {
        UserInfoEntity user = userInfoService.getUserByUniqueId(uniqueId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserInfoEntity>> getAllUsers() {
        List<UserInfoEntity> users = userInfoService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
