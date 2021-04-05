package com.j2c.j2c.service.domain.user;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpEvent {

    @NonNull
    private final String userEmail;

    @NonNull
    private final UUID tokenId;

}
