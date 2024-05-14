package com.sparta.spartaschedule.entity;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
public class Schedule {
    private Long id;
    private String title;
    private String contents;
    private String responsibility;
    private String password;
    private Date createdDate;

    public Schedule(ScheduleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.responsibility = requestDto.getResponsibility();
        this.password = requestDto.getPassword();
        this.createdDate = requestDto.getCreatedDate();
    }
}
