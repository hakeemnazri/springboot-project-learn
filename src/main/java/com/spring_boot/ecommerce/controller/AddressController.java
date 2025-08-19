package com.spring_boot.ecommerce.controller;

import com.spring_boot.ecommerce.model.User;
import com.spring_boot.ecommerce.payload.AddressDTO;
import com.spring_boot.ecommerce.service.AddressService;
import com.spring_boot.ecommerce.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(AddressDTO addressDTO){
        User user = authUtil.loggedInUser();

        AddressDTO address = addressService.createAddress(addressDTO, user);

        return new ResponseEntity<AddressDTO>(address, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(){

        List<AddressDTO> addresses= addressService.getAddresses();

        return new ResponseEntity<List<AddressDTO>>(addresses, HttpStatus.FOUND);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(
            @PathVariable Long addressId
    ){
        AddressDTO address = addressService.getAddress(addressId);

        return new ResponseEntity<AddressDTO>(address, HttpStatus.FOUND);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<List<AddressDTO>> getUserAddress(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> userAddresses = addressService.getUserAddresses(user);

        return new ResponseEntity<List<AddressDTO>>(userAddresses, HttpStatus.FOUND);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(
            @PathVariable Long addressId,
            @RequestBody AddressDTO addressDTO
    ){
        AddressDTO updatedAddress = addressService.updateAddressById(addressId, addressDTO);

        return new ResponseEntity<AddressDTO>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddressById(
            @PathVariable Long addressId
    ){
        String message =addressService.deleteAddressById(addressId);

        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}
