package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.service.ProductService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
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
    public BaseResponse<ProductDTO.GetDetail> getProduct(@PathVariable("productIdx") String productIdx){
            try {
                ProductDTO.GetDetail Detail = productService.getProduct(productIdx);
                return new BaseResponse<>(Detail);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            } catch (IOException e) {
                e.printStackTrace();
                return new BaseResponse<>(null);
            }

        }

    // 상품 비교
    @ResponseBody
    @GetMapping("/comparison/{productIdx1}/{productIdx2}")
    public BaseResponse<List<ProductDTO.GetDetail>> compareProduct(@PathVariable("productIdx1") String productIdx1, @PathVariable("productIdx2") String productIdx2){
        try {
            ProductDTO.GetDetail Detail1 = productService.getProduct(productIdx1);
            ProductDTO.GetDetail Detail2 = productService.getProduct(productIdx2);
            List<ProductDTO.GetDetail> compare = new ArrayList<>();
            compare.add(Detail1);
            compare.add(Detail2);
            return new BaseResponse<>(compare);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            return new BaseResponse<>(null);
        }

    }
    @ResponseBody
    @GetMapping("/category")
    public BaseResponse<List<ProductDTO.GetProductList>> getCategory(@RequestBody ProductDTO.GetCategoryRes getCategoryRes){
        try {
            List<ProductDTO.GetProductList> getProductList = productService.getCategoryList(getCategoryRes);
            return new BaseResponse<>(getProductList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/all")
    public BaseResponse<List<ProductDTO.GetProductList>> getAll(){
        try {
            List<ProductDTO.GetProductList> getProductList = productService.getAll();
            return new BaseResponse<>(getProductList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    * 아리 : review
    * */
    @ResponseBody
    @PostMapping("/review")
    public BaseResponse<String> postReview(Principal principal, @RequestBody ProductDTO.GetProductReview product){
        try {
            productService.postReview(principal, product);
            return new BaseResponse<>("리뷰 작성을 완료했습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/setLike")
    public BaseResponse<String> postLike(Principal principal, @RequestBody ProductDTO.PostsetLike product){
        try {
            productService.postLike(principal, product);
            return new BaseResponse<>("좋아요 목록에 추가되었습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/review/{productIdx}")
    public BaseResponse<List<ProductDTO.ReviewList>> getReview(@PathVariable("productIdx") String productIdx){
        try{
            List<ProductDTO.ReviewList> getReviewList = productService.getReviewList(productIdx);
            return new BaseResponse<>(getReviewList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
