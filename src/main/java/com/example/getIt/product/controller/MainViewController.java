package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.DTO.SpecDTO;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.service.ProductService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/main")
public class MainViewController {
    private ProductService productService;
    private ProductRepository productRepository;

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

    @ResponseBody
    @PostMapping("/findspec")
    public BaseResponse<List<ProductDTO.GetSpecResultList>> getFindSpec(@RequestBody SpecDTO.FindSpec spec) {
        List<ProductDTO.GetSpecResultList> getSpecResults = ProductService.getPickedSpec(spec);
        return new BaseResponse<>(getSpecResults);

    }
}
