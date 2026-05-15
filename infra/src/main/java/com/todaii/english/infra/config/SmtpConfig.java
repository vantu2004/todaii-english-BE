package com.todaii.english.infra.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class SmtpConfig {
  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private int mailPort;

  @Value("${spring.mail.username}")
  private String mailUsername;

  @Value("${spring.mail.password}")
  private String mailPassword;

  @Value("${spring.mail.protocol}")
  private String mailProtocol;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private boolean mailStartTlsEnable;

  @Bean
  public JavaMailSender createMailSender() {

    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    mailSender.setHost(mailHost);
    mailSender.setPort(mailPort);
    mailSender.setUsername(mailUsername);
    mailSender.setPassword(mailPassword);
    mailSender.setProtocol(mailProtocol);

    Properties props = mailSender.getJavaMailProperties();

    props.put("mail.transport.protocol", mailProtocol);
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", String.valueOf(mailStartTlsEnable));

    return mailSender;
  }
}
