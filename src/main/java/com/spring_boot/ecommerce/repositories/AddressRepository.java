package com.spring_boot.ecommerce.repositories;

import com.spring_boot.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
