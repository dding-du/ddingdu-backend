package com.ddingdu.chatbot_backend.domain.users.repository;

import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    /**
     * 이메일로 사용자 조회
     */
    Optional<Users> findByEmail(String email);

    /**
     * 학번으로 사용자 조회
     */
    Optional<Users> findByMjuId(String mjuId);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 학번 존재 여부 확인
     */
    boolean existsByMjuId(String mjuId);
}
