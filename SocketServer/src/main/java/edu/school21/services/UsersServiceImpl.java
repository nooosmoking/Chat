package edu.school21.services;

import edu.school21.models.User;
import edu.school21.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("userService")
@Transactional
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean signUp(String login, String password)  {
        if (login.isEmpty() || password.isEmpty()) {
            return false;
        }
        usersRepository.save(new User(login, passwordEncoder.encode( password), null, null));
        return true;
    }

    @Override
    public boolean signIn(String login, String password) {
        Optional<User> optionalUser = usersRepository.findByLogin(login);
        if(optionalUser.isPresent()){
            return passwordEncoder.matches(password, optionalUser.get().getPassword());
        }
        return false;
    }
}
