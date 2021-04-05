package com.j2c.j2c.service.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(
        prefix="spring.mail",
        name="host",
        matchIfMissing = true)
public class NoOpMailSender implements MailSender {

    @Override
    public void sendVerificationEmail(final String toEmail, final UUID tokenId) {}

}
