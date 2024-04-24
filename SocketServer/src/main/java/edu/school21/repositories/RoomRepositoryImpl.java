package edu.school21.repositories;

import edu.school21.models.Chatroom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;

@Repository("roomRepository")
public class RoomRepositoryImpl implements RoomRepository{
    private final DataSource dataSource;

    @Autowired
    public RoomRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Chatroom> findAll() {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "SELECT * FROM rooms";
        RowMapper<Chatroom> roomRowMapper = (r, i) -> {
            Chatroom rowRoom = new Chatroom();
            rowRoom.setName(r.getString("name"));
            rowRoom.setUserList(new LinkedList<>());
            return rowRoom;
        };
        return jdbcTemplate.query(query, roomRowMapper);
    }

    @Override
    public boolean save(Chatroom entity) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        String query = "INSERT INTO rooms (name) VALUES (:name);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("name", entity.getName()));
        return true;
    }

    @Override
    public void update(Chatroom entity) {

    }

    @Override
    public void delete(String name) {

    }
}
