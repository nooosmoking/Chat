package edu.school21.services;

import edu.school21.models.User;

public interface UsersService {
    User signUp(String login, String password);
    User signIn(String login, String password);
}
