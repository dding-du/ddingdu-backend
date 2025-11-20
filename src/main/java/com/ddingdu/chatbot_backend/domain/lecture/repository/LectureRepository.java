package com.ddingdu.chatbot_backend.domain.lecture.repository;

import com.ddingdu.chatbot_backend.domain.lecture.entity.Lecture;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    @Query(
        value = "SELECT * FROM lecture WHERE MATCH(lecture_name) AGAINST(:keyword IN BOOLEAN MODE)",
        nativeQuery = true
    )
    List<Lecture> searchByLectureName(@Param("keyword") String keyword);

    List<Lecture> findByProfessorNameContaining(String professorName);

    List<Lecture> findByLectureCode(String lectureCode);
}
