package ru.coursework.validation;

import java.util.regex.Pattern;

public class UserDataValidation {

    public boolean isUserEmailValid(String email) {
        String pattern = "[A-aZ-z0-9+_.-]+@[A-aZ-z]+\\.[a-z]+";
        return Pattern.matches(pattern, email);
    }

    public boolean isUserNumberValid(String number) {
        String pattern = "\\+7\\d+";
        return number.length() == 12 && Pattern.matches(pattern, number);
    }
}
