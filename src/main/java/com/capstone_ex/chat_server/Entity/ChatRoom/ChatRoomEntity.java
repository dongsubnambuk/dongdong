package com.capstone_ex.chat_server.Entity.ChatRoom;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "chat_room", indexes = {
        @Index(name = "idx_chat_name", columnList = "chatName"),
        @Index(name = "idx_creator_id", columnList = "creator_id")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatName;

    @Column(name = "creator_id", nullable = false)
    private String creatorId; // 채팅방 생성자의 ID만 저장

    @ManyToMany
    @JoinTable(
            name = "chatroom_users",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "unique_id")
    )
    private Set<UserInfoEntity> users = new HashSet<>(); // users 필드를 초기화

}
