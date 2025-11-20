package com.ddingdu.chatbot_backend.domain.lecture.repository;

import com.ddingdu.chatbot_backend.domain.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

}
