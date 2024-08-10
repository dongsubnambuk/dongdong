package com.capstone_ex.chat_server.Entity.User;

import com.capstone_ex.chat_server.Entity.ChatRoom.MessageEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_message", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_message_id", columnList = "message_id"),
        @Index(name = "idx_is_read", columnList = "isRead")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfoEntity user;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private MessageEntity message;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime readTime;
}
