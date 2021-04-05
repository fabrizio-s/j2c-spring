package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.*;
import java.util.List;

public interface UserService {

    long total();

    UserDTO find(@NotNull Long userId);

    boolean exists(@Email @NotBlank String email);

    UserDTO findWithAuthenticationDetails(@Email @NotBlank String email);

    Page<UserDTO> findAll(@NotNull Pageable pageable);

    Page<UserAddressDTO> findAddresses(@NotNull Long userId, @NotNull Pageable pageable);

    UserAddressDTO findAddress(@NotNull Long userId, @NotNull Long addressId);

    Page<OrderDTO> findOrders(@NotNull Long userId, @NotNull Pageable pageable);

    UserVerificationTokenDTO findUserVerificationToken(@NotNull Long userId);

    List<? extends PaymentMethodDTO> findPaymentMethods(@NotNull Long customerId);

    UserDTO create(CreateUserForm userForm);

    UserDTO update(Long userId, UpdateUserForm userForm);

    void delete(Long userId);

    UserDTO signUp(SignUpForm signUpForm);

    UserDTO verify(VerifyUserForm form);

    UserDTO changeEmail(Long userId, ChangeUserEmailForm form);

    UserDTO changePassword(Long userId, ChangeUserPasswordForm form);

    UserAddressDTO createAddress(Long userId, CreateAddressForm form);

    UserAddressDTO updateAddress(Long userId, Long addressId, UpdateAddressForm form);

    void deleteAddress(Long userId, Long addressId);

    void deletePaymentMethod(Long userId, String paymentMethodId);

}
