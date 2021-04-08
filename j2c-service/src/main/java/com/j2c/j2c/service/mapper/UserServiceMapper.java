package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.*;
import com.j2c.j2c.service.dto.OrderDTO;
import com.j2c.j2c.service.dto.UserAddressDTO;
import com.j2c.j2c.service.dto.UserDTO;
import com.j2c.j2c.service.dto.UserVerificationTokenDTO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserServiceMapper {

    private final UserDTOMapper userDTOMapper;
    private final RoleDTOMapper roleDTOMapper;
    private final UserAddressDTOMapper userAddressDTOMapper;
    private final OrderDTOMapper orderDTOMapper;
    private final UserVerificationTokenDTOMapper verificationTokenDTOMapper;

    public UserDTO toUserDTO(final User user) {
        return userDTOMapper.fromEntity(user).build();
    }

    public UserDTO toUserDTOWithRole(final User user) {
        final UserDTO.UserDTOBuilder builder = userDTOMapper.fromEntity(user);
        setRoleDTO(builder, user.getRole());
        return builder.build();
    }

    public UserDTO toUserDTOWithAuthDetails(@NonNull final User user) {
        final UserDTO.UserDTOBuilder builder = userDTOMapper.fromEntity(user);
        setRoleDTO(builder, user.getRole());
        builder.password(user.getPassword());
        return builder.build();
    }

    public Page<UserDTO> toUserDTO(final Page<User> users) {
        return userDTOMapper.fromEntities(users);
    }

    public UserAddressDTO toUserAddressDTO(@NonNull final UserAddress address) {
        return userAddressDTOMapper.fromEntity(address).build();
    }

    public Page<UserAddressDTO> toUserAddressDTO(final Page<UserAddress> addresses) {
        return userAddressDTOMapper.fromEntities(addresses);
    }

    public Page<OrderDTO> toOrderDTO(final Page<Order> orders) {
        return orderDTOMapper.fromEntities(orders);
    }

    public UserVerificationTokenDTO toUserVerificationTokenDTO(@NonNull final UserVerificationToken verificationToken) {
        return verificationTokenDTOMapper.fromEntity(verificationToken).build();
    }

    private void setRoleDTO(final UserDTO.UserDTOBuilder builder, final Role role) {
        final Set<String> authorities = role.getAuthorities().stream()
                .map(Authority::getValue)
                .collect(Collectors.toSet());
        builder.role(
                roleDTOMapper.fromEntity(role)
                        .authorities(authorities)
                        .build()
        );
    }

}
