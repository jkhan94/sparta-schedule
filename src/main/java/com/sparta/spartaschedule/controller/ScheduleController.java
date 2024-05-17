package com.sparta.spartaschedule.controller;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Swagger 링크: http://localhost:8080/swagger-ui/index.html#/
@RestController
@RequestMapping("/api")
@Tag(name = "Schedule CRUD", description = "스케줄 등록, 조회, 수정, 삭제 컨트롤러")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/schedules")
    @Operation(summary = "스케줄 등록", description = "제목, 내용, 담당자, 비밀번호, 작성일을 전달받아 스케줄을 디비에 저장할 때 사용하는 API")
    @Parameters({
            @Parameter(name = "title", description = "할 일 제목", example = "스프링 입문 과제"),
            @Parameter(name = "contents", description = "할 일 내용", example = "요구사항에 맞춰서 스케줄 CRUD 구현"),
            @Parameter(name = "responsibility", description = "담당자", example = "수강생A"),
            @Parameter(name = "password", description = "스케줄 수정 또는 삭제 시 필요한 비밀번호", example = "540"),
            @Parameter(name = "createdDate", description = "스케줄 작성일", example = "2024-05-15"),
    })
    public ScheduleResponseDto createSchedule(@RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.createSchedule(requestDto);
    }

    @GetMapping("/schedules")
    @Operation(summary = "스케줄 조회", description = "디비에 저장된 스케줄의 제목, 내용, 담당자, 작성일을 조회할 때 사용하는 API")
    public List<ScheduleResponseDto> getSchedules() {
        return scheduleService.getSchedules();
    }

    @PutMapping("/schedules/{id}")
    @Operation(summary = "스케줄 수정", description = "입력받은 비밀번호와 디비의 비밀번호가 일치할 경우 전달받은 스케줄 아이디의 일정을 수정.")
    public ScheduleResponseDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.updateSchedule(id, requestDto);
    }

    @DeleteMapping("/schedules/{id}")
    @Operation(summary = "스케줄 삭제", description = "입력받은 비밀번호와 디비의 비밀번호가 일치할 경우 전달받은 스케줄 아이디의 일정을 삭제")
    public void deleteSchedule(@PathVariable Long id, @RequestBody ScheduleRequestDto requestDto) {
        scheduleService.deleteSchedule(id, requestDto.getPassword());
    }

}
