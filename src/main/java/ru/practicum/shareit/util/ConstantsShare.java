package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public  class ConstantsShare {
    public static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
    public static final Sort sortDesc = Sort.by(Sort.Direction.DESC, "startBooking");
    public static final Sort sortAsc = Sort.by(Sort.Direction.ASC, "startBooking");
    public static final Locale russianLocal = new Locale("ru");


}
