package com.todaii.english.shared.utils;

import java.security.SecureRandom;

public class OtpUtils {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int OTP_LENGTH = 6;

	/** Sinh OTP 6 số ngẫu nhiên dạng String */
	public static String generateOtp() {
		int number = RANDOM.nextInt(1_000_000); // 0 -> 999999
		return String.format("%0" + OTP_LENGTH + "d", number);
	}
}
