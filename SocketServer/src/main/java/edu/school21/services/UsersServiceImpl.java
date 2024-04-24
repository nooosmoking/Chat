package edu.school21.services;

import edu.school21.models.User;
import edu.school21.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public boolean signUp(String login, String password) {
        if (usersRepository.findByLogin(login).isPresent()) {
            return false;
        }
        usersRepository.save(new User(login, passwordEncoder.encode(password), null, null, true));
        return true;
    }

    @Override
    @Transactional
    public boolean signIn(String login, String password) {
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        return optionalUser.filter(user -> passwordEncoder.matches(password, user.getPassword())).isPresent();
    }
}
