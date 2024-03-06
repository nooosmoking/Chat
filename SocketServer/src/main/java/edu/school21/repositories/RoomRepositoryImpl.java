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
import java.util.Optional;

@Repository("roomRepository")
public class RoomRepositoryImpl implements RoomRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public RoomRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
    @Override
    public Optional<Chatroom> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Chatroom> findAll() {
        String query = "SELECT * FROM rooms";
        RowMapper<Chatroom> roomRowMapper = (r, i) -> {
            Chatroom rowRoom = new Chatroom();
            rowRoom.setId(r.getLong("id"));
            rowRoom.setName(r.getString("name"));
            rowRoom.setUserList(new LinkedList<>());
            return rowRoom;
        };
        return jdbcTemplate.query(query, roomRowMapper);
    }

    @Override
    public boolean save(Chatroom entity) {
        String query = "INSERT INTO rooms (name) VALUES (:name);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("name", entity.getName()));
        return true;
    }

    @Override
    public void update(Chatroom entity) {

    }

    @Override
    public void delete(Long id) {

    }
}
