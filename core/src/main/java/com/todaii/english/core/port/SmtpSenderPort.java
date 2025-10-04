package com.todaii.english.core.port;

public interface SmtpSenderPort {
	void send(String to, String subject, String content);
}
