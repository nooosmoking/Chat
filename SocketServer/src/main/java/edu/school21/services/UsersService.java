package edu.school21.services;

import edu.school21.models.User;

public interface UsersService {
    boolean signUp(String login, String password);
    boolean signIn(String login, String password);
}
