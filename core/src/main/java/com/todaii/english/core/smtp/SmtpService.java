package com.todaii.english.core.smtp;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.SmtpSenderPort;
import com.todaii.english.shared.constants.MailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmtpService {
	private final SmtpSenderPort smtpSenderPort;

	public void sendVerifyEmail(String to, String otp) {
		String subject = "Verify your email";
		String content = MailTemplate.VERIFICATION_EMAIL_TEMPLATE.replace("{verificationCode}", otp);
		this.smtpSenderPort.send(to, subject, content);
	}

	public void sendForgotPasswordEmail(String to, String resetURL) {
		String subject = "Reset your password";
		String content = MailTemplate.PASSWORD_RESET_REQUEST_TEMPLATE.replace("{resetURL}", resetURL);
		this.smtpSenderPort.send(to, subject, content);
	}
}
