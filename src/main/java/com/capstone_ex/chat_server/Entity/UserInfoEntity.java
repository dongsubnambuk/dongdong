package com.capstone_ex.chat_server.Entity;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "user_info", indexes = @Index(name = "idx_user_id", columnList = "userId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    private String email;
    private String nickname;

    @ManyToMany
    private Set<ChatRoomEntity> chattingRoomEntities = new HashSet<>();
}
