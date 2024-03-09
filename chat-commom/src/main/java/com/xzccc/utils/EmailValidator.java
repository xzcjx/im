package com.xzccc.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidator {
    private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public boolean isValidEmail(String str) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return pattern.matcher(str).matches();
    }
}
