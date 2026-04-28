package ru.coursework.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
