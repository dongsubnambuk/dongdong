package com.capstone_ex.chat_server.Entity.User;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "user_info", indexes = {
        @Index(name = "idx_user_id", columnList = "unique_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column(name = "unique_id")
    private String uniqueId;

}

