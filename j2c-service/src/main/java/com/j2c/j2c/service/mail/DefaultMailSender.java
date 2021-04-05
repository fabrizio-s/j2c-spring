package com.j2c.j2c.service.mail;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnExpression("!'${spring.mail.host:}'.isBlank()")
public class DefaultMailSender implements MailSender {

    private final JavaMailSender javaMailSender;
    private final String verificationURL;
    private final String from;
    private final String smtpUsername;

    public DefaultMailSender(
            final JavaMailSender javaMailSender,
            @Value("${j2c.mail.verification-url}") @NonNull final String verificationURL,
            @Value("${j2c.mail.from}") final String from,
            @Value("${spring.mail.username}") @NonNull final String smtpUsername
    ) {
        this.javaMailSender = javaMailSender;
        this.verificationURL = verificationURL;
        this.from = from;
        this.smtpUsername = smtpUsername;
    }

    @Async
    @Override
    public void sendVerificationEmail(@NonNull final String toEmail, @NonNull final UUID tokenId) {
        final SimpleMailMessage mailMessage = newVerificationMailMessage(toEmail, tokenId);
        javaMailSender.send(mailMessage);
    }

    private SimpleMailMessage newVerificationMailMessage(final String toEmail, final UUID tokenId) {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setFrom(from());
        mailMessage.setText("To confirm your account, please click here : "
                + verificationURL + "?token=" + tokenId);
        return mailMessage;
    }

    private String from() {
        return from != null ? from : smtpUsername;
    }

}
