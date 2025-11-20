package com.ddingdu.chatbot_backend.domain.take.repository;

import com.ddingdu.chatbot_backend.domain.lecture.entity.Lecture;
import com.ddingdu.chatbot_backend.domain.take.entity.Take;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakeRepository extends JpaRepository<Take, Long> {
    Optional<Take> findByUserAndLecture(Users user, Lecture lecture);

}
