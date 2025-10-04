package com.todaii.english.infra.smtp;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.todaii.english.core.port.SmtpSenderPort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmtpSenderAdapter implements SmtpSenderPort {
	private final JavaMailSender javaMailSender;

	@Override
	public void send(String to, String subject, String content) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			JavaMailSenderImpl impl = (JavaMailSenderImpl) this.javaMailSender;
			
			String from = impl.getUsername();
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, true); // true = HTML

			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Cannot send mail", e);
		}
	}
}
