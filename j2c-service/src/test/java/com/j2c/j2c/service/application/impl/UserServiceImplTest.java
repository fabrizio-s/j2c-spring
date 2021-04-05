package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.Role;
import com.j2c.j2c.domain.entity.User;
import com.j2c.j2c.domain.entity.UserAddress;
import com.j2c.j2c.domain.entity.UserVerificationToken;
import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.service.dto.UserAddressDTO;
import com.j2c.j2c.service.input.ChangeUserEmailForm;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.exception.InvalidInputException;
import com.j2c.j2c.service.exception.ResourceAlreadyExistsException;
import com.j2c.j2c.service.exception.ResourceNotFoundException;
import com.j2c.j2c.service.exception.ServiceException;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.service.test.BaseServiceTest;
import com.j2c.j2c.service.test.MockBeanProvider;
import com.j2c.j2c.service.test.MockEntity;
import com.j2c.j2c.service.test.UserServiceStubber;
import com.neovisionaries.i18n.CountryCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.ADDRESS_DOES_NOT_BELONG_TO_USER;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.VALID_USER_EMAIL_BUT_NULL_PASSWORD;
import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class UserServiceImplTest extends BaseServiceTest {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private UserServiceStubber stubber;

    @Autowired
    private MockBeanProvider mockBeanProvider;

    @Test
    public void create_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void create_BlankEmail_ShouldThrowInvalidInputException() {
        final CreateUserForm form = hpCreateUserForm()
                .email("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void create_InvalidEmail_ShouldThrowInvalidInputException() {
        final CreateUserForm form = hpCreateUserForm()
                .email("this is an invalid email !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must be a well-formed email address"))
        );
    }

    @Test
    public void create_BlankPassword_ShouldThrowInvalidInputException() {
        final CreateUserForm form = hpCreateUserForm()
                .password("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void create_NullRole_ShouldThrowInvalidInputException() {
        final CreateUserForm form = hpCreateUserForm()
                .role(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.create(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("role must not be null"))
        );
    }

    @Test
    public void create_RoleDoesNotExist_ShouldThrowResourceNotFoundException() {
        final CreateUserForm form = hpCreateUserForm().build();

        stubber.create()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().matches("Role of type '\\w+' does not exist"));
    }

    @Test
    public void create_EmailAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        final CreateUserForm form = hpCreateUserForm().build();

        stubber.create()
                .role(roleOfType(form.getRole()))
                .emailAlreadyExists(true)
                .stub();

        final ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().matches(String.format(USER_EMAIL_ALREADY_EXISTS, form.getEmail())));
    }

    @Test
    public void create_EmailIsValidButPasswordIsNull_ShouldThrowServiceException() {
        final CreateUserForm form = hpCreateUserForm()
                .email("tester@example.com")
                .password(null)
                .build();

        stubber.create()
                .role(roleOfType(form.getRole()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.create(form)
        );
        assertTrue(exception.getMessage().matches(String.format(VALID_USER_EMAIL_BUT_NULL_PASSWORD, ".+")));
    }

    @Test
    public void create_HappyPath_ShouldReturnCreatedUser() {
        final CreateUserForm form = hpCreateUserForm().build();

        stubber.create()
                .role(roleOfType(form.getRole()))
                .stub();

        final UserDTO userDTO = service.create(form);

        assertNotNull(userDTO);
        assertEquals(form.getEmail(), userDTO.getEmail());
        assertEquals(form.isEnabled(), userDTO.isEnabled());
        assertEquals(form.isVerified(), userDTO.isVerified());
        assertEquals(form.getRole(), userDTO.getRole().getType());

        verify(mockBeanProvider.getUserRepository(), times(1))
                .save(any(User.class));
    }

    @Test
    public void update_NullUserId_ShouldThrowInvalidInputException() {
        final UpdateUserForm form = hpUpdateUserForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void update_NullForm_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void update_BlankEmail_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm()
                .email("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void update_BlankPassword_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm()
                .password("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.update(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void update_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm().build();

        stubber.update()
                .role(roleOfType(form.getRole()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(userId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void update_RoleDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm().build();

        stubber.update()
                .user(userWithId(userId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(userId, form)
        );
        assertTrue(exception.getMessage().matches("Role of type '\\w+' does not exist"));
    }

    @Test
    public void update_EmailAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm().build();

        stubber.update()
                .user(userWithId(userId))
                .role(roleOfType(form.getRole()))
                .emailAlreadyExists(true)
                .stub();

        final ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.update(userId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(USER_EMAIL_ALREADY_EXISTS, form.getEmail())));
    }

    @Test
    public void update_PasswordIsNullAndEmailIsValidButUserHasNullPassword_ShouldThrowServiceException() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm()
                .password(null)
                .build();

        stubber.update()
                .user(
                        MockEntity.user()
                                .id(userId)
                                .nullPassword(true)
                                .build()
                )
                .role(roleOfType(form.getRole()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.update(userId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(VALID_USER_EMAIL_BUT_NULL_PASSWORD, userId)));
    }

    @Test
    public void update_HappyPath_ShouldReturnUpdatedUser() {
        final Long userId = 1L;
        final UpdateUserForm form = hpUpdateUserForm().build();

        stubber.update()
                .user(userWithId(userId))
                .role(roleOfType(form.getRole()))
                .stub();

        final UserDTO userDTO = service.update(userId, form);

        assertNotNull(userDTO);
        assertEquals(form.getEmail(), userDTO.getEmail());
        assertEquals(form.getEnabled(), userDTO.isEnabled());
        assertEquals(form.getVerified(), userDTO.isVerified());
        assertEquals(form.getRole(), userDTO.getRole().getType());
    }

    @Test
    public void delete_NullUserId_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.delete(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void delete_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;

        stubber.delete()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(userId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void delete_UserHasCheckout_ShouldThrowServiceException() {
        final Long userId = 1L;

        stubber.delete()
                .user(userWithId(userId))
                .hasCheckout(true)
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.delete(userId)
        );
        assertTrue(exception.getMessage().matches(String.format(REMOVE_USER_HAS_CHECKOUT, userId)));
    }

    @Test
    public void delete_HappyPath_ShouldDeleteUser() {
        final Long userId = 1L;

        stubber.delete()
                .user(userWithId(userId))
                .stub();

        service.delete(userId);

        verify(mockBeanProvider.getUserRepository(), times(1))
                .delete(any(User.class));
    }

    @Test
    public void signUp_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.signUp(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void signUp_NullEmail_ShouldThrowInvalidInputException() {
        final SignUpForm form = hpSignUpForm()
                .email(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.signUp(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void signUp_BlankEmail_ShouldThrowInvalidInputException() {
        final SignUpForm form = hpSignUpForm()
                .email("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.signUp(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void signUp_NullPassword_ShouldThrowInvalidInputException() {
        final SignUpForm form = hpSignUpForm()
                .password(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.signUp(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void signUp_BlankPassword_ShouldThrowInvalidInputException() {
        final SignUpForm form = hpSignUpForm()
                .password("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.signUp(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void signUp_RoleDoesNotExist_ShouldThrowResourceNotFoundException() {
        final SignUpForm form = hpSignUpForm().build();

        stubber.signUp()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.signUp(form)
        );
        assertTrue(exception.getMessage().matches("Role of type '\\w+' does not exist"));
    }

    @Test
    public void signUp_EmailAlreadyExists_ShouldThrowServiceException() {
        final SignUpForm form = hpSignUpForm().build();

        stubber.signUp()
                .role(roleOfType(RoleType.Customer))
                .emailAlreadyExists(true)
                .stub();

        final ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.signUp(form)
        );
        assertTrue(exception.getMessage().matches(String.format(USER_EMAIL_ALREADY_EXISTS, form.getEmail())));
    }

    @Test
    public void signUp_HappyPath_ShouldReturnCreatedUser() {
        final SignUpForm form = hpSignUpForm().build();

        stubber.signUp()
                .role(roleOfType(RoleType.Customer))
                .stub();

        final UserDTO userDTO = service.signUp(form);

        assertNotNull(userDTO);
        assertEquals(form.getEmail(), userDTO.getEmail());
        assertTrue(userDTO.isEnabled());
        assertFalse(userDTO.isVerified());

        verify(mockBeanProvider.getUserRepository(), times(1))
                .save(any(User.class));
        verify(mockBeanProvider.getMailSender(), times(1))
                .sendVerificationEmail(anyString(), any(UUID.class));
    }

    @Test
    public void verify_NullForm_ShouldThrowInvalidInputException() {
        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.verify(null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void verify_NullVerificationTokenId_ShouldThrowInvalidInputException() {
        final VerifyUserForm form = hpVerifyUserForm()
                .tokenId(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.verify(form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("token id must not be null"))
        );
    }

    @Test
    public void verify_VerificationTokenDoesNotExist_ShouldThrowResourceNotFoundException() {
        final VerifyUserForm form = hpVerifyUserForm().build();

        stubber.verify()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.verify(form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user verification token", form.getTokenId())));
    }

    @Test
    public void verify_HappyPath_ShouldReturnVerifiedUser() {
        final VerifyUserForm form = hpVerifyUserForm().build();

        stubber.verify()
                .verificationToken(verificationTokenWithId(form.getTokenId()))
                .stub();

        final UserDTO userDTO = service.verify(form);

        assertNotNull(userDTO);
        assertTrue(userDTO.isVerified());

        verify(mockBeanProvider.getUserVerificationTokenRepository(), times(1))
                .delete(any(UserVerificationToken.class));
    }

    @Test
    public void changeEmail_NullUserId_ShouldThrowInvalidInputException() {
        final ChangeUserEmailForm form = hpChangeUserEmailForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changeEmail(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void changeEmail_NullForm_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changeEmail(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void changeEmail_NullEmail_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm()
                .email(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changeEmail(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void changeEmail_BlankEmail_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm()
                .email("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changeEmail(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must not be blank"))
        );
    }

    @Test
    public void changeEmail_InvalidEmail_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm()
                .email("This is an invalid email !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changeEmail(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("email must be a well-formed email address"))
        );
    }

    @Test
    public void changeEmail_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm().build();

        stubber.changeEmail()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.changeEmail(userId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void changeEmail_EmailAlreadyExists_ShouldThrowServiceException() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm().build();

        stubber.changeEmail()
                .user(userWithId(userId))
                .emailAlreadyExists(true)
                .stub();

        final ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.changeEmail(userId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(USER_EMAIL_ALREADY_EXISTS, form.getEmail())));
    }

    @Test
    public void changeEmail_HappyPath_ShouldReturnUpdatedUser() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm().build();

        stubber.changeEmail()
                .user(userWithId(userId))
                .stub();

        final UserDTO userDTO = service.changeEmail(userId, form);

        assertNotNull(userDTO);
        assertEquals(form.getEmail(), userDTO.getEmail());
    }

    @Test
    public void changeEmail_UserCurrentlyHasNullEmail_ShouldSendVerificationEmail() {
        final Long userId = 1L;
        final ChangeUserEmailForm form = hpChangeUserEmailForm().build();

        stubber.changeEmail()
                .user(
                        MockEntity.user()
                                .id(userId)
                                .nullEmail(true)
                                .build()
                )
                .stub();

        service.changeEmail(userId, form);

        verify(mockBeanProvider.getMailSender(), times(1))
                .sendVerificationEmail(anyString(), any(UUID.class));
    }

    @Test
    public void changePassword_NullUserId_ShouldThrowInvalidInputException() {
        final ChangeUserPasswordForm form = hpChangeUserPasswordForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changePassword(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void changePassword_NullForm_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changePassword(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void changePassword_NullPassword_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final ChangeUserPasswordForm form = hpChangeUserPasswordForm()
                .password(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changePassword(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void changePassword_BlankPassword_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final ChangeUserPasswordForm form = hpChangeUserPasswordForm()
                .password("      ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.changePassword(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("password must not be blank"))
        );
    }

    @Test
    public void changePassword_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final ChangeUserPasswordForm form = hpChangeUserPasswordForm().build();

        stubber.changePassword()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.changePassword(userId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void changePassword_HappyPath_ShouldReturnUpdatedUser() {
        final Long userId = 1L;
        final ChangeUserPasswordForm form = hpChangeUserPasswordForm().build();

        stubber.changePassword()
                .user(userWithId(userId))
                .stub();

        final UserDTO userDTO = service.changePassword(userId, form);

        assertNotNull(userDTO);
    }

    @Test
    public void createAddress_NullUserId_ShouldThrowInvalidInputException() {
        final CreateAddressForm form = hpCreateAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void createAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void createAddress_NullFirstName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .firstName(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .firstName("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void createAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .firstName("!nv4l!d n4m3")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullLastName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .lastName(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .lastName("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void createAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .lastName("!nv4l!d n4m3")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullStreetAddress1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .streetAddress1(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .streetAddress1("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .streetAddress2("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void createAddress_NullCountry_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .country(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country must not be null"))
        );
    }

    @Test
    public void createAddress_NullCountryArea_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .countryArea(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .countryArea("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void createAddress_NullCity_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .city(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .city("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .cityArea("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void createAddress_NullPostalCode_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .postalCode(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .postalCode("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void createAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .postalCode("!nv4l!d p0st4l c0d3!")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void createAddress_NullPhone1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .phone1(null)
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .phone1("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void createAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .phone1("this is an invalid phone number !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void createAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .phone2("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void createAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm()
                .phone2("this is an invalid phone number !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void createAddress_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm().build();

        stubber.createAddress()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createAddress(userId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void createAddress_HappyPath_ShouldReturnSavedAddress() {
        final Long userId = 1L;
        final CreateAddressForm form = hpCreateAddressForm().build();

        stubber.createAddress()
                .user(userWithId(userId))
                .stub();

        final UserAddressDTO addressDTO = service.createAddress(userId, form);

        assertNotNull(addressDTO);
    }

    @Test
    public void updateAddress_NullUserId_ShouldThrowInvalidInputException() {
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(null, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void updateAddress_NullAddressId_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, null, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address id must not be null"))
        );
    }

    @Test
    public void updateAddress_NullForm_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("form must not be null"))
        );
    }

    @Test
    public void updateAddress_BlankFirstName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .firstName("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("first name must not be blank"))
        );
    }

    @Test
    public void updateAddress_FirstNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .firstName("Inv4lid N4m3 !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("first name must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankLastName_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .lastName("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("last name must not be blank"))
        );
    }

    @Test
    public void updateAddress_LastNameDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .lastName("Inv4lid N4m3 !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("last name must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankStreetAddress1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .streetAddress1("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 1 must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankStreetAddress2_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .streetAddress2("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("street address 2 must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCountryArea_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .countryArea("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("country area must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCity_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .city("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankCityArea_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .cityArea("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("city area must not be blank"))
        );
    }

    @Test
    public void updateAddress_BlankPostalCode_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .postalCode("    ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("postal code must not be blank"))
        );
    }

    @Test
    public void updateAddress_PostalCodeDoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .postalCode("Invalid postal code !%£")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("postal code must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankPhone1_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .phone1("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 1 must not be blank"))
        );
    }

    @Test
    public void updateAddress_Phone1DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .phone1("Invalid phone !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 1 must match \".+\""))
        );
    }

    @Test
    public void updateAddress_BlankPhone2_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .phone2("   ")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("phone 2 must not be blank"))
        );
    }

    @Test
    public void updateAddress_Phone2DoesNotMatchPattern_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm()
                .phone2("Invalid phone !")
                .build();

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.matches("phone 2 must match \".+\""))
        );
    }

    @Test
    public void updateAddress_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        stubber.updateAddress()
                .userAddress(userAddressWithIdForUser(addressId, MockEntity.user().build()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void updateAddress_AddressDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        stubber.updateAddress()
                .user(userWithId(userId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user address", addressId)));
    }

    @Test
    public void updateAddress_AddressDoesNotBelongToUser_ShouldThrowServiceException() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        stubber.updateAddress()
                .user(userWithId(userId))
                .userAddress(userAddressWithIdForUser(addressId, MockEntity.user().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.updateAddress(userId, addressId, form)
        );
        assertTrue(exception.getMessage().matches(String.format(ADDRESS_DOES_NOT_BELONG_TO_USER, addressId, userId)));
    }

    @Test
    public void updateAddress_HappyPath_ShouldReturnUpdatedAddress() {
        final Long userId = 1L;
        final Long addressId = 1L;
        final UpdateAddressForm form = hpUpdateAddressForm().build();

        final User user = userWithId(userId);
        stubber.updateAddress()
                .user(user)
                .userAddress(userAddressWithIdForUser(addressId, user))
                .stub();

        final UserAddressDTO addressDTO = service.updateAddress(userId, addressId, form);

        assertNotNull(addressDTO);
    }

    @Test
    public void deleteAddress_NullUserId_ShouldThrowInvalidInputException() {
        final Long addressId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteAddress(null, addressId)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void deleteAddress_NullAddressId_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deleteAddress(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("address id must not be null"))
        );
    }

    @Test
    public void deleteAddress_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final Long addressId = 1L;

        stubber.deleteAddress()
                .userAddress(userAddressWithIdForUser(addressId, MockEntity.user().build()))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteAddress(userId, addressId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void deleteAddress_AddressDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final Long addressId = 1L;

        stubber.deleteAddress()
                .user(userWithId(userId))
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteAddress(userId, addressId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user address", addressId)));
    }

    @Test
    public void deleteAddress_AddressDoesNotBelongToUser_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final Long addressId = 1L;

        stubber.deleteAddress()
                .user(userWithId(userId))
                .userAddress(userAddressWithIdForUser(addressId, MockEntity.user().build()))
                .stub();

        final ServiceException exception = assertThrows(
                ServiceException.class,
                () -> service.deleteAddress(userId, addressId)
        );
        assertTrue(exception.getMessage().matches(String.format(ADDRESS_DOES_NOT_BELONG_TO_USER, addressId, userId)));
    }

    @Test
    public void deleteAddress_HappyPath_ShouldDeleteAddress() {
        final Long userId = 1L;
        final Long addressId = 1L;

        final User user = userWithId(userId);
        stubber.deleteAddress()
                .user(user)
                .userAddress(userAddressWithIdForUser(addressId, user))
                .stub();

        service.deleteAddress(userId, addressId);
    }

    @Test
    public void deletePaymentMethod_NullUserId_ShouldThrowInvalidInputException() {
        final String paymentMethodId = "abc123";

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deletePaymentMethod(null, paymentMethodId)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("user id must not be null"))
        );
    }

    @Test
    public void deletePaymentMethod_NullPaymentMethodId_ShouldThrowInvalidInputException() {
        final Long userId = 1L;

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deletePaymentMethod(userId, null)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("payment method id must not be blank"))
        );
    }

    @Test
    public void deletePaymentMethod_BlankPaymentMethodId_ShouldThrowInvalidInputException() {
        final Long userId = 1L;
        final String paymentMethodId = "   ";

        final InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> service.deletePaymentMethod(userId, paymentMethodId)
        );
        assertTrue(
                exception.getErrors().stream()
                        .anyMatch(e -> e.equals("payment method id must not be blank"))
        );
    }

    @Test
    public void deletePaymentMethod_UserDoesNotExist_ShouldThrowResourceNotFoundException() {
        final Long userId = 1L;
        final String paymentMethodId = "abc_123";

        stubber.deletePaymentMethod()
                .stub();

        final ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deletePaymentMethod(userId, paymentMethodId)
        );
        assertTrue(exception.getMessage().contains(String.format(RESOURCE_NOT_FOUND, "user", userId)));
    }

    @Test
    public void deletePaymentMethod_HappyPath_ShouldDeletePaymentMethod() {
        final Long userId = 1L;
        final String paymentMethodId = "abc_123";

        stubber.deletePaymentMethod()
                .user(userWithId(userId))
                .stub();

        service.deletePaymentMethod(userId, paymentMethodId);

        verify(mockBeanProvider.getPaymentGateway(), times(1))
                .removePaymentMethod(any(User.class), anyString());
    }

    private static CreateUserForm.CreateUserFormBuilder hpCreateUserForm() {
        return CreateUserForm.builder()
                .email("tester@example.com")
                .password("p4ssw0rd")
                .enabled(true)
                .verified(true)
                .role(RoleType.Customer);
    }

    private static UpdateUserForm.UpdateUserFormBuilder hpUpdateUserForm() {
        return UpdateUserForm.builder()
                .email("tester@example.com")
                .password("p4ssw0rd")
                .enabled(true)
                .verified(true)
                .role(RoleType.Customer);
    }

    private static SignUpForm.SignUpFormBuilder hpSignUpForm() {
        return SignUpForm.builder()
                .email("tester@example.com")
                .password("p4ssw0rd");
    }

    private static CreateAddressForm.CreateAddressFormBuilder hpCreateAddressForm() {
        return CreateAddressForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .streetAddress2("Street Address 2")
                .country(CountryCode.US)
                .countryArea("Country Area")
                .city("City")
                .cityArea("City Area")
                .postalCode("ABC123")
                .phone1("123456789")
                .phone2("987654321");
    }

    private static UpdateAddressForm.UpdateAddressFormBuilder hpUpdateAddressForm() {
        return UpdateAddressForm.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .streetAddress1("Street Address 1")
                .streetAddress2("Street Address 2")
                .country(CountryCode.US)
                .countryArea("Country Area")
                .city("City")
                .cityArea("City Area")
                .postalCode("ABC123")
                .phone1("123456789")
                .phone2("987654321");
    }

    private static ChangeUserEmailForm.ChangeUserEmailFormBuilder hpChangeUserEmailForm() {
        return ChangeUserEmailForm.builder()
                .email("tester@example.com");
    }

    private static ChangeUserPasswordForm.ChangeUserPasswordFormBuilder hpChangeUserPasswordForm() {
        return ChangeUserPasswordForm.builder()
                .password("123new_password456");
    }

    private static VerifyUserForm.VerifyUserFormBuilder hpVerifyUserForm() {
        return VerifyUserForm.builder()
                .tokenId(UUID.randomUUID());
    }

    private static User userWithId(final Long id) {
        return MockEntity.user()
                .id(id)
                .build();
    }

    private static Role roleOfType(final RoleType type) {
        return MockEntity.role()
                .type(type)
                .build();
    }

    private static UserVerificationToken verificationTokenWithId(final UUID id) {
        return MockEntity.userVerificationToken()
                .id(id)
                .build();
    }

    private static UserAddress userAddressWithIdForUser(final Long id, final User user) {
        return MockEntity.userAddress()
                .id(id)
                .user(user)
                .build();
    }

}
