package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.payload.OrderDTO;
import com.spring_boot.ecommerce.payload.OrderRequestDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId, OrderRequestDTO orderRequestDTO, String paymentMethod);
}
