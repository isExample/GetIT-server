package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.service.ProductService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/main")
public class MainViewController {
    private ProductService productService;

    public MainViewController(ProductService productService){
        this.productService = productService;
    }

    @ResponseBody
    @GetMapping("/recommproducts")
    public BaseResponse<List<ProductDTO.GetProductList>> getRecommProducts(@RequestParam String topic){
        try {
            List<ProductDTO.GetProductList> getProducts = this.productService.getRecommProducts(topic);
            return new BaseResponse<>(getProducts);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
