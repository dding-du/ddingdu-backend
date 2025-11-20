package com.ddingdu.chatbot_backend.domain.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false, updatable = false) // name 속성 명시!
    private String conversationId;

    @Column(name = "role", nullable = false, updatable = false)
    private String role;

    @Lob
    @Column(name = "message", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
    private String message;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(String conversationId, String role, String message, LocalDateTime createdAt) {
        this.conversationId = conversationId;
        this.role = role;
        this.message = message;
        this.createdAt = createdAt;
    }


}
