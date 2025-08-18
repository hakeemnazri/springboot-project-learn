package com.spring_boot.ecommerce.repositories;

import com.spring_boot.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
