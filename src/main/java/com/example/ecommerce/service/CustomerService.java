package com.example.ecommerce.service;

import com.example.ecommerce.dto.CustomerDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = mapToEntity(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToDto(savedCustomer);
    }
    
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return mapToDto(customer);
    }
    
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public CustomerDto getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return mapToDto(customer);
    }
    
    private Customer mapToEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        
        Customer.Address address = new Customer.Address();
        address.setStreet(dto.getAddress().getStreet());
        address.setCity(dto.getAddress().getCity());
        address.setState(dto.getAddress().getState());
        address.setZipCode(dto.getAddress().getZipCode());
        address.setCountry(dto.getAddress().getCountry());
        customer.setAddress(address);
        
        return customer;
    }
    
    private CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setCreatedAt(customer.getCreatedAt());
        
        CustomerDto.AddressDto addressDto = new CustomerDto.AddressDto();
        addressDto.setStreet(customer.getAddress().getStreet());
        addressDto.setCity(customer.getAddress().getCity());
        addressDto.setState(customer.getAddress().getState());
        addressDto.setZipCode(customer.getAddress().getZipCode());
        addressDto.setCountry(customer.getAddress().getCountry());
        dto.setAddress(addressDto);
        
        return dto;
    }
}
