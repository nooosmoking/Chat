package edu.school21.repositories;

import edu.school21.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository("usersRepository")
public class UsersRepositoryImpl implements UsersRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UsersRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Optional<User> findById(Long id) {
        String query = "SELECT * FROM users WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<User> user = jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<>(User.class));
            return user.stream().findFirst();

    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        RowMapper<User> userRowMapper = (r, i) -> {
            User rowUser = new User();
            rowUser.setId(r.getLong("id"));
            rowUser.setLogin(r.getString("login"));
            rowUser.setPassword(r.getString("password"));
            return rowUser;
        };
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public boolean save(User entity) {
        if(findByLogin(entity.getLogin()).isPresent()){
            return false;
        }
        String query = "INSERT INTO users (login, password) VALUES (:login, :password);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("login", entity.getLogin())
                .addValue("password", entity.getPassword()));
        return true;
    }

    @Override
    public void update(User entity) {
        String query = "UPDATE users SET login = :login, password = :password WHERE id = :id;";
        jdbcTemplate.update(query, new BeanPropertySqlParameterSource(entity));
    }


    @Override
    public void delete(Long id) {
        String query = "DELETE FROM users WHERE id = :id;";
        jdbcTemplate.update(query, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        String query = "SELECT * FROM users WHERE login = :login";
        List<User> user = jdbcTemplate.query(query, new MapSqlParameterSource().addValue("login", login), new BeanPropertyRowMapper<>(User.class));
        return user.stream().findFirst();
    }
}