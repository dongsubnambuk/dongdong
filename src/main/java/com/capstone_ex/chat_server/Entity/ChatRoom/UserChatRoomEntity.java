package com.capstone_ex.chat_server.Entity.ChatRoom;

import com.capstone_ex.chat_server.Entity.UserInfoEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_chat_room") // 채팅 관리용
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfoEntity userInfoEntity;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoomEntity chatRoomEntity;
}
