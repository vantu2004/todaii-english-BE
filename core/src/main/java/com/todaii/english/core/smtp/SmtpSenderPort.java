package com.todaii.english.core.smtp;

public interface SmtpSenderPort {
	void send(String to, String subject, String content);
}
