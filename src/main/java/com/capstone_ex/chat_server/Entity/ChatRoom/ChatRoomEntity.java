package com.capstone_ex.chat_server.Entity.ChatRoom;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "chatting_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long chatRoomId;
    @Column
    private String chatName;
    @Column
    private String description;

    @ManyToMany // 유저랑 채팅방 다대다 연결
    @JoinTable(
            name = "user_chat_room",
            joinColumns = @JoinColumn(name = "chatroom_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserInfoEntity> users = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true) // 메세지랑 채팅방 일대다 연결
    private Set<MessageEntity> messages = new HashSet<>();
}
