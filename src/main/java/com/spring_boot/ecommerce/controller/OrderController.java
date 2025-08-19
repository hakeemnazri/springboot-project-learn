package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.payload.OrderDTO;
import com.spring_boot.ecommerce.payload.OrderRequestDTO;
import com.spring_boot.ecommerce.service.OrderService;
import com.spring_boot.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO
    ){
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(
                emailId,
                orderRequestDTO,
                paymentMethod
        );

        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.CREATED);
    }
}
