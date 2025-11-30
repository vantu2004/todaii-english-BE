package com.todaii.english.core.smtp;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.core.port.SmtpSenderPort;
import com.todaii.english.shared.constants.MailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmtpService {
	private final SmtpSenderPort smtpSenderPort;
	private final SettingQueryPort settingQueryPort;

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

	public void accountBannedNotice(String to, String name) {
		String contactMail = settingQueryPort.getSettingByKey("mail_username").getValue();

		String subject = "Your account has been banned";
		String content = MailTemplate.ACCOUNT_BANNED_TEMPLATE.replace("{name}", name).replace("{contactMail}",
				contactMail);
		this.smtpSenderPort.send(to, subject, content);
	}

	public void accountUnBannedNotice(String to, String name) {
		String subject = "Your account has been unbanned";
		String content = MailTemplate.ACCOUNT_UNBANNED_TEMPLATE.replace("{name}", name);
		this.smtpSenderPort.send(to, subject, content);
	}

	public void accountDeletedNotice(String to, String name) {
		String subject = "Your account has been completely deleted.";
		String content = MailTemplate.ACCOUNT_DELETED_TEMPLATE.replace("{name}", name);
		this.smtpSenderPort.send(to, subject, content);
	}
}
