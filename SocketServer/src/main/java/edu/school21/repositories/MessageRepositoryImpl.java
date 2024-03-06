package edu.school21.repositories;

import edu.school21.models.Message;
import edu.school21.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Repository("messageRepository")
public class MessageRepositoryImpl implements MessageRepository{
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public MessageRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<Message> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        String query = "SELECT m.id AS message_id, m.text, m.date_time, u.id AS user_id, u.login FROM messages m " +
                "LEFT JOIN users u ON m.sender_id = u.id";
        RowMapper<Message> messageRowMapper = (r, i) -> {
            Message rowMessage = new Message();
            rowMessage.setId(r.getLong("message_id"));
            rowMessage.setSender(new User(r.getLong("user_id"), r.getString("login"), null));
            rowMessage.setText(r.getString("text"));
            rowMessage.setTime(convertToLocalDateTime(r.getTimestamp("date_time")));
            return rowMessage;
        };
        return jdbcTemplate.query(query, messageRowMapper);
    }

    private LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        ZoneId gmtZone = ZoneId.of("GMT");
        ZoneId localZone = ZoneId.systemDefault();

        return timestamp.toLocalDateTime().atZone(gmtZone).withZoneSameInstant(localZone).toLocalDateTime();
    }


    @Override
    public boolean save(Message entity) {
        String query = "INSERT INTO messages (text, sender_id) VALUES (:text, :sender_id);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("text", entity.getText())
                .addValue("sender_id", entity.getSender().getId()));
        return true;
    }

    @Override
    public void update(Message entity) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Message> findLastCountMessages(int count) {
        String query = "SELECT m.id AS message_id, m.text, m.date_time, u.id AS user_id, u.login FROM messages m " +
                "LEFT JOIN users u ON m.sender_id = u.id "+
                "ORDER BY m.date_time "+
                "LIMIT :count";
        RowMapper<Message> messageRowMapper = (r, i) -> {
            Message rowMessage = new Message();
            rowMessage.setId(r.getLong("message_id"));
            rowMessage.setSender(new User(r.getLong("user_id"), r.getString("login"), null));
            rowMessage.setText(r.getString("text"));
            rowMessage.setTime(convertToLocalDateTime(r.getTimestamp("date_time")));
            return rowMessage;
        };
        return jdbcTemplate.query(query, new MapSqlParameterSource().addValue("count", count),messageRowMapper);
    }
}
