package com.sparta.spartaschedule.controller;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    private final JdbcTemplate jdbcTemplate;

    public ScheduleController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/schedules")
    public ScheduleResponseDto createSchedule(@RequestBody ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(requestDto);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO schedule (title, contents, responsibility, password, createdDate) VALUES (?, ?,?,?,?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, schedule.getTitle());
                    preparedStatement.setString(2, schedule.getContents());
                    preparedStatement.setString(3, schedule.getResponsibility());
                    preparedStatement.setString(4, schedule.getPassword());
                    preparedStatement.setDate(5, schedule.getCreatedDate());
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        schedule.setId(id);

        // Entity -> ResponseDto
        ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);

        return scheduleResponseDto;
    }

    @GetMapping("/schedules")
    public List<ScheduleResponseDto> getSchedules() {
        String sql = "SELECT * FROM schedule ORDER BY createdDate desc";

        return jdbcTemplate.query(sql, new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String contents = rs.getString("contents");
                String responsibility = rs.getString("responsibility");
                String password=rs.getString("password");
                Date createdDate = rs.getDate("createdDate");
                return new ScheduleResponseDto(id, title, contents, responsibility,password, createdDate);
            }
        });
    }

    @PutMapping("/schedules/{id}/{password}")
    public ScheduleResponseDto updateSchedule(@PathVariable Long id, @PathVariable String password, @RequestBody ScheduleRequestDto requestDto) {
        Schedule schedule = findById(id);
        if (schedule != null && schedule.getPassword().equals(password)) {
            String sql = "UPDATE schedule SET title = ?, contents = ?, responsibility = ? WHERE id = ?";
            jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getContents(), requestDto.getResponsibility(), id);

            schedule = findById(id);
            ScheduleResponseDto scheduleResponseDto = new ScheduleResponseDto(schedule);
            return scheduleResponseDto;
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    @DeleteMapping("/schedules/{id}/{password}")
    public void deleteSchedule(@PathVariable Long id, @PathVariable String password) {
        // 해당 메모가 DB에 존재하는지 확인
        Schedule schedule = findById(id);
        if (schedule != null && schedule.getPassword().equals(password)) {
            String sql = "DELETE FROM schedule WHERE id = ?";
            jdbcTemplate.update(sql, id);

        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    private Schedule findById(Long id) {
        // DB 조회
        String sql = "SELECT * FROM schedule WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if (resultSet.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(resultSet.getLong("id"));
                schedule.setTitle(resultSet.getString("title"));
                schedule.setContents(resultSet.getString("contents"));
                schedule.setResponsibility(resultSet.getString("responsibility"));
                schedule.setPassword(resultSet.getString("password"));
                schedule.setCreatedDate(resultSet.getDate("createdDate"));
                return schedule;
            } else {
                return null;
            }
        }, id);
    }

}
