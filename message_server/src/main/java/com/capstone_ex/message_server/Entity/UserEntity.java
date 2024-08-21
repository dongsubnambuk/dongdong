package com.capstone_ex.message_server.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Table(name = "user", indexes = {
        @Index(name = "idx_chat_user_id", columnList = "user_id, chat_room_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "message_id")
    private Long messageId;

    @ManyToMany
    @JoinTable(
            name = "message_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private Set<MessageEntity> messages = new HashSet<>();
}

