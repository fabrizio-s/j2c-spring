package com.j2c.j2c.service.domain.user;

import com.j2c.j2c.service.mail.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class DomainUserServiceEventListener {

    private final MailSender mailSender;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(final SignUpEvent event) {
        mailSender.sendVerificationEmail(event.getUserEmail(), event.getTokenId());
    }

}
