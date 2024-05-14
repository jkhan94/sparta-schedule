package com.sparta.spartaschedule.dto;

import lombok.Getter;

import java.sql.Date;

@Getter
public class ScheduleRequestDto {
    private String title;
    private String contents;
    private String responsibility;
    private String password;
    private Date createdDate;
}
