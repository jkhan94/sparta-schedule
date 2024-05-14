package com.sparta.spartaschedule.controller;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.entity.Schedule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;

// Swagger 링크: http://localhost:8080/swagger-ui/index.html#/
@RestController
@RequestMapping("/api")
@Tag(name = "Schedule CRUD", description = "스케줄 등록, 조회, 수정, 삭제 컨트롤러")
public class ScheduleController {
    private final JdbcTemplate jdbcTemplate;

    public ScheduleController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/schedules")
    @Operation(summary = "스케줄 등록", description = "제목, 내용, 담당자, 비밀번호, 작성일을 전달받아 스케줄을 디비에 저장할 때 사용하는 API")
    @Parameters({
            @Parameter(name="id", description = "스케줄 고유번호",example = "5"),
            @Parameter(name="title", description = "할 일 제목",example = "스프링 입문 과제"),
            @Parameter(name="contents", description = "할 일 내용",example = "요구사항에 맞춰서 스케줄 CRUD 구현"),
            @Parameter(name="responsibility", description = "담당자",example = "수강생A"),
            @Parameter(name="password", description = "스케줄 수정 또는 삭제 시 필요한 비밀번호",example = "540"),
            @Parameter(name="createdDate", description = "스케줄 작성일",example = "2024-05-15"),
    })
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
    @Operation(summary = "스케줄 조회", description = "디비에 저장된 스케줄의 제목, 내용, 담당자, 작성일을 조회할 때 사용하는 API")
    public List<ScheduleResponseDto> getSchedules() {
        String sql = "SELECT * FROM schedule ORDER BY createdDate desc";

        return jdbcTemplate.query(sql, new RowMapper<ScheduleResponseDto>() {
            @Override
            public ScheduleResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String contents = rs.getString("contents");
                String responsibility = rs.getString("responsibility");
//                String password=rs.getString("password");
                Date createdDate = rs.getDate("createdDate");
                return new ScheduleResponseDto(id, title, contents, responsibility, createdDate);
            }
        });
    }

    @PutMapping("/schedules/{id}/{password}")
    @Operation(summary = "스케줄 수정", description = "입력받은 비밀번호와 디비의 비밀번호가 일치할 경우 전달받은 스케줄 아이디의 일정을 수정.")
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
    @Operation(summary = "스케줄 삭제", description = "입력받은 비밀번호와 디비의 비밀번호가 일치할 경우 전달받은 스케줄 아이디의 일정을 삭제")
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
