package com.sparta.spartaschedule.service;

import com.sparta.spartaschedule.Repository.ScheduleRepository;
import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.entity.Schedule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);
        Schedule saveSchedule = scheduleRepository.create(schedule);
        // Entity -> ResponseDto
        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;
    }

    public List<ScheduleResponseDto> getSchedules() {
        return scheduleRepository.getAll();
    }

    public ScheduleResponseDto updateSchedule(Long id, String password, ScheduleRequestDto requestDto) {
        Schedule schedule = scheduleRepository.findById(id);

        if (schedule != null && schedule.getPassword().equals(password)) {
            scheduleRepository.update(id, requestDto);

            schedule = scheduleRepository.findById(id); // 수정된 내용을 불러옴
            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);
            return scheduleResponseDto;
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    public void deleteSchedule(Long id, String password) {
        // 해당 메모가 DB에 존재하는지 확인
        Schedule schedule = scheduleRepository.findById(id);
        if (schedule != null && schedule.getPassword().equals(password)) {
            scheduleRepository.delete(id);
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }


}
