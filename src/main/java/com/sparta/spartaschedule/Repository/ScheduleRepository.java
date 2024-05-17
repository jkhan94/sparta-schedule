package com.sparta.spartaschedule.Repository;

import com.sparta.spartaschedule.dto.ScheduleRequestDto;
import com.sparta.spartaschedule.dto.ScheduleResponseDto;
import com.sparta.spartaschedule.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public Schedule create(Schedule schedule) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Date date = new Date(System.currentTimeMillis());

        String sql = "INSERT INTO schedule (title, contents, responsibility, password, createdDate) VALUES (?, ?,?,?,?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, schedule.getTitle());
                    preparedStatement.setString(2, schedule.getContents());
                    preparedStatement.setString(3, schedule.getResponsibility());
                    preparedStatement.setString(4, schedule.getPassword());
                    preparedStatement.setDate(5, date);
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        schedule.setId(id);
        schedule.setCreatedDate(date);

        return schedule;
    }

    public List<ScheduleResponseDto> getAll() {
        String sql = "SELECT * FROM schedule ORDER BY createdDate desc, id desc";

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

    public void update(Long id, ScheduleRequestDto requestDto) {
        String sql = "UPDATE schedule SET title = ?, contents = ?, responsibility = ? WHERE id = ?";
        jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getContents(), requestDto.getResponsibility(), id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM schedule WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Schedule findById(Long id) {
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
