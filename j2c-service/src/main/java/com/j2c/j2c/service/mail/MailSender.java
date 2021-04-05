package com.j2c.j2c.service.mail;

import java.util.UUID;

public interface MailSender {

    void sendVerificationEmail(String toEmail, UUID tokenId);

}
