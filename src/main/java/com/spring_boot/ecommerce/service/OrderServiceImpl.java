package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.exceptions.APIException;
import com.spring_boot.ecommerce.exceptions.ResourceNotFoundException;
import com.spring_boot.ecommerce.model.*;
import com.spring_boot.ecommerce.payload.OrderDTO;
import com.spring_boot.ecommerce.payload.OrderItemDTO;
import com.spring_boot.ecommerce.payload.OrderRequestDTO;
import com.spring_boot.ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    OrderRespository orderRespository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, OrderRequestDTO orderRequestDTO, String paymentMethod) {
        //Getting user cart
        Cart cart = cartRepository.findCartByEmail(emailId);

        if(cart == null){
            throw new ResourceNotFoundException(emailId, "Cart", "emailId");
        }

        Address address = addressRepository.findById(orderRequestDTO.getAddressId()).orElseThrow(() -> new ResourceNotFoundException(orderRequestDTO.getAddressId(), "Address", "addressId"));

        //create a new order with payment info

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted!");

        Payment payment = new Payment(paymentMethod, orderRequestDTO.getPgPaymentId(), orderRequestDTO.getPgStatus(), orderRequestDTO.getPgResponseMessage(), orderRequestDTO.getPgName());

        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);

        Order savedOrder = orderRespository.save(order);

        //get items from the cart into the order items

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setOrder(savedOrder);
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItems.add(orderItem);
        }

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        //update product stock
        cart.getCartItems().forEach(item -> {
            Integer quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

        //clear the cart
        cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
        });
        //send back the order summary

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(
                modelMapper.map(item, OrderItemDTO.class)
        ));
        orderDTO.setAddressId(address.getAddressId());

        return orderDTO;
    }
}
