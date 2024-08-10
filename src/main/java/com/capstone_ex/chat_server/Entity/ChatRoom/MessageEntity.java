package com.capstone_ex.chat_server.Entity.ChatRoom;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.capstone_ex.chat_server.Entity.User.UserMessageEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message", indexes = {
        @Index(name = "idx_chatroom_id", columnList = "chatroom_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_send_time", columnList = "sendTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sendTime;

    @Column(nullable = false)
    private int readCount;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfoEntity user;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserMessageEntity> userMessages = new HashSet<>();
}

