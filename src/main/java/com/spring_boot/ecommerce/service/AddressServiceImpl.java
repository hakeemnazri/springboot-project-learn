package com.spring_boot.ecommerce.service;

import com.spring_boot.ecommerce.exceptions.ResourceNotFoundException;
import com.spring_boot.ecommerce.model.Address;
import com.spring_boot.ecommerce.model.User;
import com.spring_boot.ecommerce.payload.AddressDTO;
import com.spring_boot.ecommerce.repositories.AddressRepository;
import com.spring_boot.ecommerce.repositories.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(
            @Valid @RequestBody AddressDTO addressDTO,
            User user
    ) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addresses = user.getAddresses();
        addresses.add(address);
        user.setAddresses(addresses);
        address.setUser(user);

        addressRepository.save(address);

        return modelMapper.map(addresses, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> addressDTOs = addresses.stream().map(a -> modelMapper.map(a, AddressDTO.class)).toList();
        return addressDTOs;
    }

    @Override
    public AddressDTO getAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException(addressId, "Address", "addressId"));

        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);

        return addressDTO;
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOs = addresses.stream().map(a -> modelMapper.map(a, AddressDTO.class)).toList();

        return addressDTOs;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address findAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException(addressId, "Address", "addressId"));

        findAddress.setCity(addressDTO.getCity());
        findAddress.setPincode(addressDTO.getPincode());
        findAddress.setCountry(addressDTO.getCountry());
        findAddress.setState(addressDTO.getState());
        findAddress.setStreet(addressDTO.getStreet());
        findAddress.setBuildingName(addressDTO.getBuildingName());

        Address savedAddress = addressRepository.save(findAddress);

        User user = findAddress.getUser();
        boolean isRemoved = user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));

        user.getAddresses().add(savedAddress);

        userRepository.save(user);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException(addressId, "Address", "addressId"));

        User user = address.getUser();

        user.getAddresses().removeIf(a -> a.getAddressId().equals(address.getAddressId()));

        addressRepository.delete(address);

        return "Address deleted Successfully: " + address.getAddressId();
    }


}
