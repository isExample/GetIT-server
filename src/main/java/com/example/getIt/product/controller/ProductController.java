package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.service.ProductService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public BaseResponse<ProductDTO.GetProductItemList> getCategory(@RequestParam("type") String type,@RequestParam("requirement") String  requirement){
        try {
            ProductDTO.GetProductItemList getProductList = productService.getCategoryList(type, requirement);
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

    /*
     * 아리 : 추천 검색어
     * */

    @ResponseBody
    @GetMapping("/recommend")
    public BaseResponse<List<ProductDTO.Recommend>> recommend(){
        try {
            return new BaseResponse<>(productService.recommend());
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/setLike")
    public BaseResponse<String> postLike(Principal principal, @RequestBody ProductDTO.PostsetLike productId){
        try {
            String result = this.productService.postLike(principal, productId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/review/{productIdx}")
    public BaseResponse<List<ProductDTO.ReviewList>> getReview(@PathVariable("productId") String productId){
        try{
            List<ProductDTO.ReviewList> getReviewList = productService.getReviewList(productId);
            return new BaseResponse<>(getReviewList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/delete/{reviewIdx}")
    public BaseResponse<String> deleteReview(Principal principal, @PathVariable("reviewIdx") Long reviewIdx){
        try{
            productService.deleteReview(principal, reviewIdx);
            return new BaseResponse<>("리뷰를 삭제했습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/isLike")
    public BaseResponse<ProductDTO.GetIsLike> getCategory(Principal principal, @RequestParam("productId") String  productId){
        try {
            ProductDTO.GetIsLike getProductList = productService.getProductIsLike(principal, productId);
            return new BaseResponse<>(getProductList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
