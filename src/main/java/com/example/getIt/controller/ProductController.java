package com.example.getIt.controller;

import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.DTO.UserDTO;
import com.example.getIt.entity.ProductEntity;
import com.example.getIt.service.ProductService;
import com.example.getIt.service.UserService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @ResponseBody
    @GetMapping("/list")
    public BaseResponse<List<ProductDTO.GetProduct>> getProducts(){
        try {
            List<ProductDTO.GetProduct> getProducts = this.productService.getProductAll();
            return new BaseResponse<>(getProducts);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    //상품 조회
    @ResponseBody
    @GetMapping("/{productIdx}")
    public BaseResponse<ProductDTO.GetProductRes> getProduct(@PathVariable("productIdx")Long productIdx){
        try {
            ProductDTO.GetProductRes productInfo = productService.getProduct(productIdx);
            return new BaseResponse<>(productInfo);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
