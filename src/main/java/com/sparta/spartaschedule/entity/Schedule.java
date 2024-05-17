package com.sparta.spartaschedule.entity;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name="schedule")
@Getter
@Setter
@NoArgsConstructor
public class Schedule extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false, length=255)
    private String title;
    @Column(name = "contents", nullable = false, length=500)
    private String contents;
    @Column(name = "reponsibility", nullable = false, length = 255)
    private String responsibility;
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    public Schedule(ScheduleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.responsibility = requestDto.getResponsibility();
        this.password = requestDto.getPassword();
    }

    public void update(ScheduleRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
        this.responsibility = requestDto.getResponsibility();
        this.password = requestDto.getPassword();
    }
}
