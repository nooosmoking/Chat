package edu.school21.services;

import edu.school21.models.User;
import edu.school21.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public boolean signUp(String email, String password)  {
        if (email.isEmpty() || password.isEmpty()) {
            return false;
        }
        return usersRepository.save(new User(0L, email, passwordEncoder.encode( password)));
    }

    @Override
    public boolean signIn(String email, String password) {
        return false;
    }
}
