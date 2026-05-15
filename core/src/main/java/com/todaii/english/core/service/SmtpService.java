package com.todaii.english.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.todaii.english.core.port.SmtpSenderPort;
import com.todaii.english.shared.constants.MailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmtpService {
  private final SmtpSenderPort smtpSenderPort;

  @Value("${spring.mail.username}")
  private String mailUsername;

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
    String subject = "Your account has been banned";
    String content =
        MailTemplate.ACCOUNT_BANNED_TEMPLATE
            .replace("{name}", name)
            .replace("{contactMail}", mailUsername);
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

  public void accountCreatedNotice(String to, String name) {
    String subject = "Your account has been created.";
    String content = MailTemplate.ACCOUNT_CREATED_BY_ADMIN_TEMPLATE.replace("{name}", name);
    this.smtpSenderPort.send(to, subject, content);
  }

  public void accountUpdatedNotice(String to, String name) {
    String subject = "Your account has been updated.";
    String content = MailTemplate.ACCOUNT_UPDATED_BY_ADMIN_TEMPLATE.replace("{name}", name);
    this.smtpSenderPort.send(to, subject, content);
  }
}
