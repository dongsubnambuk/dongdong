package com.capstone_ex.message_server.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "message", indexes = {
        @Index(name = "idx_chat_room_id", columnList = "chat_room_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userId;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column
    private Long checkCount;

    @Column
    private LocalDateTime sendTime;
}
