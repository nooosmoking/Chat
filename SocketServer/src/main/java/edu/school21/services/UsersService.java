package edu.school21.services;

public interface UsersService {
    boolean signUp(String login, String password);
    boolean signIn(String login, String password);
}
