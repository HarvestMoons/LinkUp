package com.example.linkup.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Friendships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false)
    private User friend;

    @JsonIgnore
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @Column()
    private LocalDateTime updatedAt;
}
