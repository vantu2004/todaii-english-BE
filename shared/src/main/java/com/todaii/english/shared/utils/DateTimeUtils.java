package com.todaii.english.shared.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
	private final static DateTimeFormatter SRT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");

	public static int toMillis(LocalTime time) {
		return (int) ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, time);
	}
}
