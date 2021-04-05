package com.j2c.j2c.service.test;

import com.j2c.j2c.domain.entity.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestComponent
@RequiredArgsConstructor
public class UserServiceStubber {

    private final MockBeanProvider mockBeanProvider;

    @Builder(builderClassName = "MockCreate",
            builderMethodName = "create",
            buildMethodName = "stub")
    private void _create(
            final Role role,
            final boolean emailAlreadyExists
    ) {
        mockFindRoleByType(role);
        mockUserExistsByEmail(emailAlreadyExists);
    }

    @Builder(builderClassName = "MockUpdate",
            builderMethodName = "update",
            buildMethodName = "stub")
    private void _update(
            final User user,
            final boolean emailAlreadyExists,
            final Role role
    ) {
        mockFindUserById(user);
        mockUserExistsByEmail(emailAlreadyExists);
        mockFindRoleByType(role);
    }

    @Builder(builderClassName = "MockDelete",
            builderMethodName = "delete",
            buildMethodName = "stub")
    private void _delete(
            final User user,
            final boolean hasCheckout
    ) {
        mockFindUserById(user);
        mockCheckoutExistsByCustomerId(hasCheckout);
    }

    @Builder(builderClassName = "MockSignUp",
            builderMethodName = "signUp",
            buildMethodName = "stub")
    private void _signUp(
            final Role role,
            final boolean emailAlreadyExists
    ) {
        mockFindRoleByType(role);
        mockUserExistsByEmail(emailAlreadyExists);
    }

    @Builder(builderClassName = "MockVerify",
            builderMethodName = "verify",
            buildMethodName = "stub")
    private void _verify(final UserVerificationToken verificationToken) {
        mockFindUserVerificationTokenById(verificationToken);
    }

    @Builder(builderClassName = "MockChangeEmail",
            builderMethodName = "changeEmail",
            buildMethodName = "stub")
    private void _changeEmail(
            final User user,
            final boolean emailAlreadyExists
    ) {
        mockFindUserById(user);
        mockUserExistsByEmail(emailAlreadyExists);
    }

    @Builder(builderClassName = "MockChangePassword",
            builderMethodName = "changePassword",
            buildMethodName = "stub")
    private void _changePassword(final User user) {
        mockFindUserById(user);
    }

    @Builder(builderClassName = "MockCreateAddress",
            builderMethodName = "createAddress",
            buildMethodName = "stub")
    private void _createAddress(final User user) {
        mockFindUserById(user);
    }

    @Builder(builderClassName = "MockUpdateAddress",
            builderMethodName = "updateAddress",
            buildMethodName = "stub")
    private void _updateAddress(
            final User user,
            final UserAddress userAddress
    ) {
        mockFindUserById(user);
        mockFindUserAddressById(userAddress);
    }

    @Builder(builderClassName = "MockDeleteAddress",
            builderMethodName = "deleteAddress",
            buildMethodName = "stub")
    private void _deleteAddress(
            final User user,
            final UserAddress userAddress
    ) {
        mockFindUserById(user);
        mockFindUserAddressById(userAddress);
    }

    @Builder(builderClassName = "MockDeletePaymentMethod",
            builderMethodName = "deletePaymentMethod",
            buildMethodName = "stub")
    private void _deletePaymentMethod(final User user) {
        mockFindUserById(user);
    }

    private void mockFindUserById(final User user) {
        if (user != null) {
            when(mockBeanProvider.getUserRepository().findById(user.getId()))
                    .thenReturn(Optional.of(user));
        }
    }

    private void mockFindRoleByType(final Role role) {
        if (role != null) {
            when(mockBeanProvider.getRoleRepository().findByType(role.getType()))
                    .thenReturn(Optional.of(role));
        }
    }

    private void mockFindUserAddressById(final UserAddress userAddress) {
        if (userAddress != null) {
            when(mockBeanProvider.getUserAddressRepository().findById(userAddress.getId()))
                    .thenReturn(Optional.of(userAddress));
        }
    }

    private void mockFindUserVerificationTokenById(final UserVerificationToken verificationToken) {
        if (verificationToken != null) {
            when(mockBeanProvider.getUserVerificationTokenRepository().findById(verificationToken.getId()))
                    .thenReturn(Optional.of(verificationToken));
        }
    }

    private void mockUserExistsByEmail(final boolean emailAlreadyExists) {
        if (emailAlreadyExists) {
            when(mockBeanProvider.getUserRepository().existsByEmail(anyString()))
                    .thenReturn(true);
        }
    }

    private void mockCheckoutExistsByCustomerId(final boolean hasCheckout) {
        if (hasCheckout) {
            when(mockBeanProvider.getCheckoutRepository().existsById(any(Long.class)))
                    .thenReturn(true);
        }
    }

}
