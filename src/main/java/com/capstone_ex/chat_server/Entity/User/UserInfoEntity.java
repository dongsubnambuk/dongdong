package com.capstone_ex.chat_server.Entity.User;

import com.capstone_ex.chat_server.Entity.ChatRoom.ChatRoomEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "user_info", indexes = {
        @Index(name = "idx_user_id", columnList = "userId", unique = true)
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

    private String userId;
    private String email;
    private String nickname;

    @ManyToMany(mappedBy = "users")
    @JsonManagedReference // 이 필드는 직렬화에 포함
    private Set<ChatRoomEntity> chatRooms = new HashSet<>();

}
