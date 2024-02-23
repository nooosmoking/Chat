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
        String query = "SELECT * FROM \"user\" WHERE id = :id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        List<User> user = jdbcTemplate.query(query, parameters, new BeanPropertyRowMapper<>(User.class));
            return user.stream().findFirst();

    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM \"user\"";
        RowMapper<User> userRowMapper = (r, i) -> {
            User rowUser = new User();
            rowUser.setId(r.getLong("id"));
            rowUser.setEmail(r.getString("email"));
            return rowUser;
        };
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public boolean save(User entity) {
        if(findByEmail(entity.getEmail()).isPresent()){
            return false;
        }
        String query = "INSERT INTO \"user\" (email, password) VALUES (:email, :password);";
        jdbcTemplate.update(query, new MapSqlParameterSource()
                .addValue("email", entity.getEmail())
                .addValue("password", entity.getPassword()));
        return true;
    }

    @Override
    public void update(User entity) {
        String query = "UPDATE \"user\" SET email = :email WHERE id = :id;";
        jdbcTemplate.update(query, new BeanPropertySqlParameterSource(entity));
    }


    @Override
    public void delete(Long id) {
        String query = "DELETE FROM \"user\" WHERE id = :id;";
        jdbcTemplate.update(query, new MapSqlParameterSource().addValue("id", id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String query = "SELECT * FROM \"user\" WHERE email = :email";
        List<User> user = jdbcTemplate.query(query, new MapSqlParameterSource().addValue("email", email), new BeanPropertyRowMapper<>(User.class));
        return user.stream().findFirst();
    }
}
