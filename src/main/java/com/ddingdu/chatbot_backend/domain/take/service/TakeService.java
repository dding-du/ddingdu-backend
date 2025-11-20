package com.ddingdu.chatbot_backend.domain.take.service;

import com.ddingdu.chatbot_backend.domain.lecture.entity.Lecture;
import com.ddingdu.chatbot_backend.domain.lecture.repository.LectureRepository;
import com.ddingdu.chatbot_backend.domain.take.dto.TakeRequestDto;
import com.ddingdu.chatbot_backend.domain.take.dto.TakeResponseDto;
import com.ddingdu.chatbot_backend.domain.take.entity.Take;
import com.ddingdu.chatbot_backend.domain.take.repository.TakeRepository;
import com.ddingdu.chatbot_backend.domain.users.entity.Users;
import com.ddingdu.chatbot_backend.domain.users.repository.UsersRepository;
import com.ddingdu.chatbot_backend.global.exception.CustomException;
import com.ddingdu.chatbot_backend.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TakeService {

    private final TakeRepository takeRepository;
    private final LectureRepository lectureRepository;
    private final UsersRepository usersRepository;

    public Void takeLecture(TakeRequestDto takeRequestDto) {
        Lecture lecture = lectureRepository.findById(takeRequestDto.getLectureId())
            .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        Users user = usersRepository.findById(takeRequestDto.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Take take = Take.builder()
            .lecture(lecture)
            .user(user)
            .build();
        takeRepository.save(take);

        return null;
    }

    public Void dropLecture(TakeRequestDto takeRequestDto) {
        Lecture lecture = lectureRepository.findById(takeRequestDto.getLectureId())
            .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        Users user = usersRepository.findById(takeRequestDto.getUserId())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Take take = takeRepository.findByUserAndLecture(user, lecture)
            .orElseThrow(() -> new CustomException(ErrorCode.TAKE_NOT_FOUND));

        takeRepository.delete(take);

        return null;
    }

    public List<TakeResponseDto> myLecture(Long userId) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Take> takes = user.getTakes();

        return takes.stream()
            .map(take -> new TakeResponseDto(
                take.getLecture().getId(),
                take.getLecture().getLectureName(),
                take.getLecture().getProfessorName()
            ))
            .toList();
    }
}
