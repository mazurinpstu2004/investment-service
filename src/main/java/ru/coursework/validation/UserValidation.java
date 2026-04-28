package ru.coursework.validation;

import java.util.regex.Pattern;

public class UserValidation {

    public boolean isLoginValid(String login) {
        String pattern = "[A-aZ-z0-9]+";
        return Pattern.matches(pattern, login);
    }

    public boolean isPasswordValid(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}";
        return Pattern.matches(pattern, password);
    }
}
