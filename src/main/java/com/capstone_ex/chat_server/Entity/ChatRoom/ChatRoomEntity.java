package com.capstone_ex.chat_server.Entity.ChatRoom;

import com.capstone_ex.chat_server.Entity.User.UserInfoEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "chat_room")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatName;
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserInfoEntity creator; // 채팅방 생성자

    @ManyToMany
    @JoinTable(
            name = "chatroom_users",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserInfoEntity> users = new HashSet<>(); // users 필드를 초기화

    // 기존 필드와 메서드들...

    // 새로운 메서드: 채팅방 생성자 ID를 반환
    public String getCreatorId() {
        return creator != null ? creator.getUserId() : null;
    }
}
