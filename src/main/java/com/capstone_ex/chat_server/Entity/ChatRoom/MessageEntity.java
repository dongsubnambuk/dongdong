package com.capstone_ex.chat_server.Entity.ChatRoom;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
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
    private boolean check;

    private String content;
    private Long messageId;
    private LocalDateTime sendTime;
    private String userId;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoomEntity chatRoom;
}
