package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.OrderService;
import com.j2c.j2c.service.application.UserService;
import com.j2c.j2c.service.dto.*;
import com.j2c.j2c.service.input.*;
import com.j2c.j2c.web.security.annotation.HasReadAccess;
import com.j2c.j2c.web.security.annotation.HasUserWriteAccess;
import com.j2c.j2c.web.security.annotation.IsOwnerOrHasReadAccess;
import com.j2c.j2c.web.security.annotation.IsOwnerOrHasWriteAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.j2c.j2c.domain.enums.Authorities.READ_ACCESS;
import static com.j2c.j2c.domain.enums.Authorities.WRITE_USERS;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints related to system users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping(value = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves all users",
            description = "Requires " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<UserDTO> getAll(@ParameterObject final Pageable pageable) {
        return userService.findAll(pageable);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.HEAD,
            produces = MediaType.APPLICATION_JSON_VALUE, params = "email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Checks whether a user with the specified email already exists")
    public ResponseEntity<Void> emailExists(@RequestParam(value="email") final String email) {
        if (userService.exists(email)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/api/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single user by id",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public UserDTO get(@PathVariable final Long userId) {
        return userService.find(userId);
    }

    @GetMapping(value = "/api/users/{userId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the addresses belonging to the specified user",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<UserAddressDTO> getAddresses(@PathVariable final Long userId, @ParameterObject final Pageable pageable) {
        return userService.findAddresses(userId, pageable);
    }

    @GetMapping(value = "/api/users/{userId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves a single address belonging to the specified user",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public UserAddressDTO getAddress(
            @PathVariable final Long userId,
            @PathVariable final Long addressId
    ) {
        return userService.findAddress(userId, addressId);
    }

    @GetMapping(value = "/api/users/{userId}/orders",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the orders belonging to the specified user",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderDTO> getOrders(@PathVariable final Long userId, @ParameterObject final Pageable pageable) {
        return userService.findOrders(userId, pageable);
    }

    @GetMapping(value = "/api/users/{userId}/orders/{orderId}/lines",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the order lines belonging to the specified user's order",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public Page<OrderLineDTO> getOrderLines(
            @PathVariable final Long userId,
            @PathVariable final Long orderId,
            @ParameterObject final Pageable pageable
    ) {
        return orderService.findLines(orderId, pageable);
    }

    @GetMapping(value = "/api/users/{userId}/payment-methods",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasReadAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Retrieves the payment methods belonging to the specified user",
            description = "Requires ownership of the resource or " + READ_ACCESS + " authority (Viewer, Staff, Admin).")
    public List<? extends PaymentMethodDTO> getPaymentMethods(@PathVariable final Long userId) {
        return userService.findPaymentMethods(userId);
    }

    @PostMapping(value = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasUserWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates a user",
            description = "Returns the created user." +
                    "The email must be unique." +
                    "Requires " + WRITE_USERS + " authority (Admin).")
    public UserDTO create(@RequestBody final CreateUserForm payload) {
        return userService.create(payload);
    }

    @PatchMapping(value = "/api/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @HasUserWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the user with the specified id",
            description = "Returns the updated user." +
                    "The email must be unique." +
                    "Requires " + WRITE_USERS + " authority (Admin).")
    public UserDTO update(
            @PathVariable final Long userId,
            @RequestBody final UpdateUserForm payload
    ) {
        return userService.update(userId, payload);
    }

    @DeleteMapping("/api/users/{userId}")
    @IsOwnerOrHasWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the user with the specified id",
            description = "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public void delete(@PathVariable final Long userId) {
        userService.delete(userId);
    }

    @PostMapping(value = "/api/signup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Creates a user with Customer privileges",
            description = "Returns the created user." +
                    "The email must be unique.")
    public UserDTO signUp(@RequestBody final SignUpForm payload) {
        return userService.signUp(payload);
    }

    @PostMapping(value = "/api/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Verifies the user's email",
            description = "Returns the updated user.")
    public UserDTO verify(@RequestBody final VerifyUserForm payload) {
        return userService.verify(payload);
    }

    @PatchMapping(value = "/api/users/{userId}/email",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Changes the email of the user with the specified id",
            description = "Returns the updated user." +
                    "The email must be unique." +
                    "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public UserDTO changeEmail(
            @PathVariable final Long userId,
            @RequestBody final ChangeUserEmailForm payload
    ) {
        return userService.changeEmail(userId, payload);
    }

    @PatchMapping(value = "/api/users/{userId}/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Changes the password of the user with the specified id",
            description = "Returns the updated user." +
                    "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public UserDTO changePassword(
            @PathVariable final Long userId,
            @RequestBody final ChangeUserPasswordForm payload
    ) {
        return userService.changePassword(userId, payload);
    }

    @PostMapping(value = "/api/users/{userId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Creates an address for the user with the specified id",
            description = "Returns the created address." +
                    "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public UserAddressDTO createAddress(
            @PathVariable final Long userId,
            @RequestBody final CreateAddressForm payload
    ) {
        return userService.createAddress(userId, payload);
    }

    @PatchMapping(value = "/api/users/{userId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @IsOwnerOrHasWriteAccess
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Updates the address by id for the specified user",
            description = "Returns the updated address." +
                    "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public UserAddressDTO updateAddress(
            @PathVariable final Long userId,
            @PathVariable final Long addressId,
            @RequestBody final UpdateAddressForm payload
    ) {
        return userService.updateAddress(userId, addressId, payload);
    }

    @DeleteMapping("/api/users/{userId}/addresses/{addressId}")
    @IsOwnerOrHasWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the address by id for the specified user",
            description = "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public void deleteAddress(
            @PathVariable final Long userId,
            @PathVariable final Long addressId
    ) {
        userService.deleteAddress(userId, addressId);
    }

    @DeleteMapping("/api/users/{userId}/payment-methods/{paymentMethodId}")
    @IsOwnerOrHasWriteAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Deletes the payment method by id for the specified user",
            description = "Requires ownership of the resource or " + WRITE_USERS + " authority (Admin).")
    public void deletePaymentMethod(
            @PathVariable final Long userId,
            @PathVariable final String paymentMethodId
    ) {
        userService.deletePaymentMethod(userId, paymentMethodId);
    }

}
