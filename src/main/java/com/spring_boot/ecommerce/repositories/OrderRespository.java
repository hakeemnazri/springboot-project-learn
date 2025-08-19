package com.spring_boot.ecommerce.repositories;

import com.spring_boot.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRespository extends JpaRepository<Order, Long> {
}
