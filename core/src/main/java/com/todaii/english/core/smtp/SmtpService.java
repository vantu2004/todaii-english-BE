package com.todaii.english.core.smtp;

import org.springframework.stereotype.Service;

import com.todaii.english.shared.constants.MailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmtpService {
	private final SmtpSenderPort smtpSenderPort;

	public void sendVerifyEmail(String to, String OTP) {
		String subject = "Verify your email";
		String content = MailTemplate.VERIFICATION_EMAIL_TEMPLATE.replace("{verificationCode}", OTP.toString());
		this.smtpSenderPort.send(to, subject, content);
	}
}
