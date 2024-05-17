package com.sparta.spartaschedule.service;

import com.sparta.spartaschedule.Repository.ScheduleRepository;
import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.entity.Schedule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);
        Schedule saveSchedule = scheduleRepository.save(schedule);
        // Entity -> ResponseDto
        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;
    }

    public List<ScheduleResponseDto> getSchedules() {
        return scheduleRepository.findAllByOrderByCreatedDateDesc().stream().map(ScheduleResponseDto::new).toList();
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long id, String password, ScheduleRequestDto requestDto) {
        Schedule schedule = findSchedule(id);

        if (schedule.getPassword().equals(password)) {
            schedule.update(requestDto);

            schedule = findSchedule(id);; // 수정된 내용을 불러옴
            return new ScheduleResponseDto(schedule);
        } else {
            throw new IllegalArgumentException("선택한 스케줄은 존재하지 않습니다.");
        }
    }

    public void deleteSchedule(Long id, String password) {
        // 해당 메모가 DB에 존재하는지 확인
        Schedule schedule = findSchedule(id);
        if (schedule.getPassword().equals(password)) {
            scheduleRepository.delete(schedule);
        }
    }

    private Schedule findSchedule(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("선택한 스케줄은 존재하지 않습니다."));
    }
}
