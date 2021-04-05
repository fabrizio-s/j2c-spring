package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressSDJRepository
        extends JpaRepository<UserAddress, Long> {

    Page<UserAddress> findAllByUserId(Long userId, Pageable pageable);

}
