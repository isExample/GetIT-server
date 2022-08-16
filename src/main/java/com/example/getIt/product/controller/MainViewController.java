package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.DTO.SpecDTO;
import com.example.getIt.product.entity.SpecEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.SpecRepository;
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
    private SpecRepository specRepository;

    public MainViewController(ProductService productService){
        this.productService = productService;
    }

    @ResponseBody
    @GetMapping("/recommproducts")
    public BaseResponse<ProductDTO.GetRecommItemList> getRecommProducts(){
        try {
            ProductDTO.GetRecommItemList getProducts = this.productService.getRecommProducts();
            return new BaseResponse<>(getProducts);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/savespec")
    public BaseResponse<SpecEntity> specsave(@RequestBody SpecDTO.FindSpec findspec){
        SpecEntity specEntity = this.productService.specsave(findspec);
        return new BaseResponse<>(specEntity);
    }
    @ResponseBody
    @GetMapping("/findspec")
    public BaseResponse<List<SpecDTO.GetSpec>> getFindSpec(@RequestBody SpecDTO.FindSpec spec) {
        List<SpecDTO.GetSpec> getSpecResults = ProductService.getSpecList(spec);
        return new BaseResponse<>(getSpecResults);
    }
}
