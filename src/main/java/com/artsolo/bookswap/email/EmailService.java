package com.artsolo.bookswap.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final static Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void send(String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom("jojekgobbany@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Error occurred during sending message", e);
            throw new IllegalStateException("Error occurred during sending message");
        }
    }

    @Override
    @Async
    public void sendEmailConfirmation(String to, String name, String jwtToken) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("token", jwtToken);

            String process = templateEngine.process("confirm-email", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom("jojekgobbany@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Confirm your email");
            mimeMessageHelper.setText(process, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Error occurred during sending message", e);
            throw new IllegalStateException("Error occurred during sending message");
        }
    }

    @Override
    @Async
    public void sendResetPasswordConfirmation(String to, String name, String jwtToken) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("token", jwtToken);

            String process = templateEngine.process("reset-password", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom("jojekgobbany@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Reset your password");
            mimeMessageHelper.setText(process, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Error occurred during sending message", e);
            throw new IllegalStateException("Error occurred during sending message");
        }
    }


}
