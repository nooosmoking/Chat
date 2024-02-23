package edu.school21.services;

public interface UsersService {
    boolean signUp(String email, String password);
    boolean signIn(String email, String password);
}
