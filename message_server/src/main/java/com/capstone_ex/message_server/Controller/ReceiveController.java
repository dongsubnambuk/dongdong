package com.capstone_ex.message_server.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/message/receiver")
public class ReceiveController {
    @GetMapping("/check")
    public ResponseEntity<?> checkConnection(){
        return ResponseEntity.ok("Success");
    }
}
