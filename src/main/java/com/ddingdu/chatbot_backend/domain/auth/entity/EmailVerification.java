package com.ddingdu.chatbot_backend.domain.auth.entity;

import com.ddingdu.chatbot_backend.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "email_verification")
@Getter
@Builder(toBuilder = true)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;  // 인증할 이메일 (@mju.ac.kr)

    @Column(nullable = false, length = 6)
    private String code;   // 6자리 인증 코드

    @Builder.Default
    @Column(nullable = false)
    private Boolean verified = false;  // 인증 완료 여부

    @Builder
    public EmailVerification(String email, String code, Boolean verified) {
        this.email = email;
        this.code = code;
        this.verified = verified;
    }
}

