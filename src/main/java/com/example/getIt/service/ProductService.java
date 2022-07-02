package com.example.getIt.service;


import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.entity.ProductEntity;
import com.example.getIt.repository.ProductRepository;
import com.example.getIt.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.JwtService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private JwtService jwtService;

    public ProductService(ProductRepository productRepository, JwtService jwtService){
        this.productRepository = productRepository;
        this.jwtService = jwtService;
    }
    public List<ProductDTO.GetProduct> getProductAll() throws BaseException {
        List<ProductDTO.GetProduct> getProducts = this.productRepository.findByOrderByCreatedAt();
        return getProducts;
    }
}
