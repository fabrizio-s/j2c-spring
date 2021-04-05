package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.domain.entity.UserAddress;
import com.j2c.j2c.domain.entity.UserVerificationToken;
import com.j2c.j2c.domain.repository.OrderRepository;
import com.j2c.j2c.domain.repository.UserAddressRepository;
import com.j2c.j2c.domain.repository.UserRepository;
import com.j2c.j2c.domain.repository.UserVerificationTokenRepository;
import com.j2c.j2c.service.application.UserService;
import com.j2c.j2c.service.domain.user.DomainUserService;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.gateway.PaymentGateway;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.mapper.UserServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceMapper mapper;
    private final DomainUserService domainService;
    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final UserVerificationTokenRepository verificationTokenRepository;

    @Override
    public long total() {
        return userRepository.count();
    }

    @Override
    public UserDTO find(@NotNull final Long userId) {
        final User user = userRepository.findById(userId);
        return mapper.toUserDTO(user);
    }

    @Override
    public boolean exists(@Email @NotBlank final String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findWithAuthenticationDetails(@Email @NotBlank final String email) {
        final User user = userRepository.findByEmail(email);
        return mapper.toUserDTOWithAuthDetails(user);
    }

    @Override
    public Page<UserDTO> findAll(@NotNull final Pageable pageable) {
        final Page<User> users = userRepository.findAll(pageable);
        return mapper.toUserDTO(users);
    }

    @Override
    public Page<UserAddressDTO> findAddresses(@NotNull final Long userId, @NotNull final Pageable pageable) {
        userRepository.verifyExistsById(userId);
        final Page<UserAddress> addresses = addressRepository.findAllByUserId(userId, pageable);
        return mapper.toUserAddressDTO(addresses);
    }

    @Override
    public UserAddressDTO findAddress(@NotNull final Long userId, @NotNull final Long addressId) {
        final User user = userRepository.findById(userId);
        final UserAddress address = addressRepository.findById(addressId)
                .verifyBelongsToUser(user);
        return mapper.toUserAddressDTO(address);
    }

    @Override
    public Page<OrderDTO> findOrders(@NotNull final Long userId, @NotNull final Pageable pageable) {
        final Page<Order> orders = orderRepository.findAllByCustomerId(userId, pageable);
        return mapper.toOrderDTO(orders);
    }

    @Override
    public UserVerificationTokenDTO findUserVerificationToken(@NotNull final Long userId) {
        final UserVerificationToken verificationToken = verificationTokenRepository.findByUserId(userId);
        return mapper.toUserVerificationTokenDTO(verificationToken);
    }

    @Override
    public List<? extends PaymentMethodDTO> findPaymentMethods(@NotNull final Long customerId) {
        final User user = userRepository.findById(customerId);
        return paymentGateway.findPaymentMethods(user);
    }

    @Override
    @Transactional
    public UserDTO create(final CreateUserForm userForm) {
        final User createdUser = domainService.create(userForm);
        return mapper.toUserDTOWithRole(createdUser);
    }

    @Override
    @Transactional
    public UserDTO update(final Long userId, final UpdateUserForm userForm) {
        final User updatedUser = domainService.update(userId, userForm);
        return mapper.toUserDTOWithRole(updatedUser);
    }

    @Override
    public void delete(final Long userId) {
        domainService.delete(userId);
    }

    @Override
    public UserDTO signUp(final SignUpForm signUpForm) {
        final User signedUpUser = domainService.signUp(signUpForm);
        return mapper.toUserDTO(signedUpUser);
    }

    @Override
    public UserDTO verify(final VerifyUserForm form) {
        final User verifiedUser = domainService.verify(form);
        return mapper.toUserDTO(verifiedUser);
    }

    @Override
    public UserDTO changeEmail(final Long userId, final ChangeUserEmailForm form) {
        final User updatedUser = domainService.changeEmail(userId, form);
        return mapper.toUserDTO(updatedUser);
    }

    @Override
    public UserDTO changePassword(final Long userId, final ChangeUserPasswordForm form) {
        final User updatedUser = domainService.changePassword(userId, form);
        return mapper.toUserDTO(updatedUser);
    }

    @Override
    public UserAddressDTO createAddress(final Long userId, final CreateAddressForm form) {
        final UserAddress createdAddress = domainService.createAddress(userId, form);
        return mapper.toUserAddressDTO(createdAddress);
    }

    @Override
    public UserAddressDTO updateAddress(final Long userId, final Long addressId, final UpdateAddressForm form) {
        final UserAddress updatedAddress = domainService.updateAddress(userId, addressId, form);
        return mapper.toUserAddressDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(final Long userId, final Long addressId) {
        domainService.deleteAddress(userId, addressId);
    }

    @Override
    public void deletePaymentMethod(final Long userId, final String paymentMethodId) {
        domainService.deletePaymentMethod(userId, paymentMethodId);
    }

}
