package com.j2c.j2c.service.domain.user;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.domain.repository.*;
import com.j2c.j2c.service.exception.ResourceAlreadyExistsException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.gateway.PaymentGateway;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.mapper.AddressVOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.j2c.j2c.domain.util.J2cUtils.optional;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.REMOVE_USER_HAS_CHECKOUT;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.USER_EMAIL_ALREADY_EXISTS;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class DomainUserService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final UserVerificationTokenRepository verificationTokenRepository;
    private final CheckoutRepository checkoutRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentGateway paymentGateway;
    private final AddressVOMapper addressMapper;

    public User create(@NotNull @Valid final CreateUserForm form) {
        final Role role = roleRepository.findByType(form.getRole());

        final String encodedPassword = optional(form.getPassword())
                .map(passwordEncoder::encode)
                .orElse(null);

        return userRepository.save(
                User.builder()
                        .email(verifyDoesNotExist(form.getEmail()))
                        .password(encodedPassword)
                        .enabled(form.isEnabled())
                        .verified(form.isVerified())
                        .role(role)
                        .build()
        );
    }

    public User update(@NotNull final Long userId, @NotNull @Valid final UpdateUserForm form) {
        final User user = userRepository.findById(userId);

        optional(form.getPassword()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        optional(form.getEmail()).map(this::verifyDoesNotExist).ifPresent(user::setEmail);
        optional(form.getEnabled()).ifPresent(user::setEnabled);
        optional(form.getVerified()).ifPresent(user::setVerified);
        optional(form.getRole()).map(roleRepository::findByType).ifPresent(user::setRole);

        return user;
    }

    public void delete(@NotNull final Long userId) {
        final User user = userRepository.findById(userId);

        if (checkoutRepository.existsById(userId)) {
            throw new ServiceException(String.format(REMOVE_USER_HAS_CHECKOUT, userId));
        }

        userRepository.remove(user);
    }

    public User signUp(@NotNull @Valid final SignUpForm form) {
        final Role role = roleRepository.findByType(RoleType.Customer);

        final String encodedPassword = passwordEncoder.encode(form.getPassword());

        final User user = userRepository.save(
                User.builder()
                        .email(verifyDoesNotExist(form.getEmail()))
                        .password(encodedPassword)
                        .enabled(true)
                        .verified(false)
                        .role(role)
                        .build()
        );

        sendVerificationEmail(user);

        return user;
    }

    public User verify(@NotNull @Valid final VerifyUserForm form) {
        final UserVerificationToken verificationToken = verificationTokenRepository.findById(form.getTokenId());

        final User user = verificationToken.getUser();

        user.setVerified(true);

        verificationTokenRepository.remove(verificationToken);

        return user;
    }

    public User changeEmail(@NotNull final Long userId, @NotNull @Valid final ChangeUserEmailForm form) {
        final User user = userRepository.findById(userId);

        final boolean hasNullEmail = user.getEmail() == null;

        user.setEmail(verifyDoesNotExist(form.getEmail()));

        if (hasNullEmail) {
            sendVerificationEmail(user);
        }

        return user;
    }

    public User changePassword(@NotNull final Long userId, @NotNull @Valid final ChangeUserPasswordForm form) {
        final User user = userRepository.findById(userId);

        user.setPassword(passwordEncoder.encode(form.getPassword()));

        return user;
    }

    public UserAddress createAddress(@NotNull final Long userId, @NotNull @Valid final CreateAddressForm form) {
        final User user = userRepository.findById(userId);

        final Address address = addressMapper.fromCreateForm(form);

        return addressRepository.save(user.addAddress(address));
    }

    public UserAddress updateAddress(
            @NotNull final Long userId,
            @NotNull final Long addressId,
            @NotNull @Valid final UpdateAddressForm form
    ) {
        final User user = userRepository.findById(userId);

        final UserAddress userAddress = addressRepository.findById(addressId)
                .verifyBelongsToUser(user);

        final Address address = addressMapper.fromUpdateForm(userAddress.getAddress(), form);

        userAddress.setAddress(address);

        return userAddress;
    }

    public void deleteAddress(@NotNull final Long userId, @NotNull final Long addressId) {
        final User user = userRepository.findById(userId);

        final UserAddress userAddress = addressRepository.findById(addressId)
                .verifyBelongsToUser(user);

        user.removeAddress(userAddress);
    }

    public void deletePaymentMethod(@NotNull final Long userId, @NotBlank final String paymentMethodId) {
        final User user = userRepository.findById(userId);

        paymentGateway.removePaymentMethod(user, paymentMethodId);
    }

    private String verifyDoesNotExist(final String email) {
        if (email == null) {
            return null;
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(String.format(USER_EMAIL_ALREADY_EXISTS, email));
        }
        return email;
    }

    private void sendVerificationEmail(final User user) {
        final UserVerificationToken token = verificationTokenRepository.save(
                UserVerificationToken.builder()
                        .user(user)
                        .build()
        );

        eventPublisher.publishEvent(
                SignUpEvent.builder()
                        .userEmail(user.getEmail())
                        .tokenId(token.getId())
                        .build()
        );
    }

}
