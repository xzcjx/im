package com.xzccc.utils;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
  private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

  public boolean isValidEmail(String str) {
    Pattern pattern = Pattern.compile(EMAIL_REGEX);
    return pattern.matcher(str).matches();
  }
}
