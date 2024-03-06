package edu.school21.services;

import edu.school21.models.User;
import edu.school21.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service("userService")
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User signUp(String login, String password)  {
        if (login.isEmpty() || password.isEmpty()) {
            return null;
        }
        usersRepository.save(new User(0L, login, passwordEncoder.encode( password)));
        return usersRepository.findByLogin(login).get();
    }

    @Override
    public User signIn(String login, String password) {
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(passwordEncoder.matches(password, user.getPassword())){
                return user;}
        }
        return null;
    }
}
