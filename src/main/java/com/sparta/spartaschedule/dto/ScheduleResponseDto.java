package com.sparta.spartaschedule.dto;

import com.sparta.spartaschedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String contents;
    private String responsibility;
//    private String password;
    private LocalDateTime createdDate;

    public ScheduleResponseDto(Schedule schedule) {
        this.id = schedule.getId();
        this.title = schedule.getTitle();
        this.contents = schedule.getContents();
        this.responsibility = schedule.getResponsibility();
//        this.password = schedule.getPassword();
        this.createdDate = schedule.getCreatedDate();
    }
}
