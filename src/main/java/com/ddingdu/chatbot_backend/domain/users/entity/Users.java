package com.ddingdu.chatbot_backend.domain.users.entity;

import com.ddingdu.chatbot_backend.domain.users.enums.Major;
import com.ddingdu.chatbot_backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "mju_id", nullable = false, unique = true, length = 20)
    private String mjuId;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Major major;

    @Builder
    public Users(String mjuId, String name, String email, String password, Major major) {
        this.mjuId = mjuId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.major = major;
    }
}
