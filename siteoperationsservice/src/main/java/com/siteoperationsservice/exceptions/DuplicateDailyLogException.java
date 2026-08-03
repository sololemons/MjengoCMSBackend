package com.siteoperationsservice.exceptions;

import java.time.LocalDate;

public class DuplicateDailyLogException extends RuntimeException {
    public DuplicateDailyLogException(String message) {
        super(message);
    }
}
