package edu.school21.services;

import edu.school21.models.User;

public interface UsersService {
    User signUp(String email, String password);
    User signIn(String email, String password);
}
