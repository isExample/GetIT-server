package com.example.getIt.product.service;


import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.product.entity.ReviewEntity;
import com.example.getIt.product.entity.UserProductEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.ReviewRepository;
import com.example.getIt.product.repository.UserProductRepository;
import com.example.getIt.product.repository.WebsiteRepository;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WebsiteRepository websiteRepository;
    private UserRepository userRepository;
    private String clientId;
    private String clientSecret;
    private ReviewRepository reviewRepository;
    private UserProductRepository userProductRepository;
//    private static final String clientId = "YOUR_CLIENT_ID";
//    private static final String clientSecret = "YOUR_CLIENT_SECRET";

    public ProductService(ProductRepository productRepository, WebsiteRepository websiteRepository, UserRepository userRepository,
                          ReviewRepository reviewRepository, UserProductRepository userProductRepository, @Value("${clientId}") String clientId, @Value("${clientSecret}") String clientSecret){
        this.productRepository = productRepository;
        this.websiteRepository = websiteRepository;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.userProductRepository = userProductRepository;
    }
    public List<ProductDTO.GetProduct> getProductAll() throws BaseException {
        List<ProductDTO.GetProduct> getProducts = this.productRepository.findByOrderByCreatedAt();
        return getProducts;
    }

    public void getProduct(String productIdx) throws BaseException, IOException {
        String url = "https://search.shopping.naver.com/catalog/"+productIdx;
        Document doc = Jsoup.connect(url).get();
        Elements namecontents = doc.select("div.top_summary_title__15yAr > h2");
        Elements contents = doc.select("div.top_summary_title__15yAr > div:nth-child(4) >span");
        String[] productinfo = new String[contents.size()];
        System.out.println(namecontents.text());
        for (int i = 0; i < contents.size(); i++) {
            productinfo[i]=contents.get(i).text();
        }
        for (int j = 0; j< productinfo.length; j++){
            boolean b = productinfo[j].contains("CPU") || productinfo[j].contains("크기") || productinfo[j].contains("램") || productinfo[j].contains("무게") || productinfo[j].contains("품목");
            boolean c = productinfo[j].contains("내장메모리") || productinfo[j].contains("통신규격") || productinfo[j].contains("운영체제");
            boolean d = productinfo[j].contains("SSD")||productinfo[j].contains("HDD");
            boolean s = productinfo[j].contains("품목")||productinfo[j].contains("출력")||productinfo[j].contains("단자")||productinfo[j].contains("무게");
            if(productinfo[j].contains("스마트폰")){
                if(b||c){
                    System.out.println(productinfo[j]);
                }
            }
            else if(productinfo[j].contains("데스크탑")){
                if(b||d){
                    System.out.println(productinfo[j]);
                }
            }
            else if(productinfo[j].contains("패드")){
                if(b||c){
                    System.out.println(productinfo[j]);
                }
            }
            else if(productinfo[j].contains("스피커")){
                if(s){
                    System.out.println(productinfo[j]);
                }
            }
            else{
                if(b||c||d){
                    System.out.println(productinfo[j]);
                }
            }
            }
        }

    public List<ProductDTO.GetProductList> getCategoryList(ProductDTO.GetCategoryRes getCategoryRes) throws BaseException {
        try {
            String apiUrl = "https://openapi.naver.com/v1/search/shop.json?query=";
            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", clientId);
            headers.add("X-Naver-Client-Secret", clientSecret);
            String body = "";
            apiUrl += getCategoryRes.getType()+","+getCategoryRes.getRequirement();
            apiUrl = apiUrl.replace(",null", "");
            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> responseEntity = rest.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
            JSONObject rjson = new JSONObject(responseEntity.getBody());
            JSONArray items = rjson.getJSONArray("items");
            if(items.isEmpty()){
                throw new Exception();
            }else{
                List<ProductDTO.GetProductList> result = new ArrayList<>();
                for (int i=0; i<items.length(); i++){
                    JSONObject eachItem = (JSONObject) items.get(i);
                    ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                    product.setProductUrl("https://search.shopping.naver.com/catalog/"+product.getProductUrl());
                    result.add(product);
                }
                return result;
            }
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
    }

    public List<ProductDTO.GetProductList> getAll() throws BaseException {
        try {
            String[] categories = {"노트북", "핸드폰", "태블릿", "스피커", "데스크탑"};
            List<ProductDTO.GetProductList> result = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                String query = categories[i];
                ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
                String encode = StandardCharsets.UTF_8.decode(buffer).toString();

                URI uri = UriComponentsBuilder
                        .fromUriString("https://openapi.naver.com")
                        .path("/v1/search/shop.json")
                        .queryParam("query", encode)
                        .queryParam("display", 20)
                        .encode()
                        .build()
                        .toUri();

                RestTemplate restTemplate = new RestTemplate();

                RequestEntity<Void> req = RequestEntity
                        .get(uri)
                        .header("X-Naver-Client-Id", clientId)
                        .header("X-Naver-Client-Secret", clientSecret)
                        .build();

                ResponseEntity<String> responseEntity = restTemplate.exchange(req, String.class);
                JSONObject rjson = new JSONObject(responseEntity.getBody());
                JSONArray items = rjson.getJSONArray("items");
                if(items.isEmpty()){
                    throw new Exception();
                }else {
                    for (int j = 0; j < items.length(); j++) {
                        JSONObject eachItem = (JSONObject) items.get(j);
                        ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                        product.setProductUrl("https://search.shopping.naver.com/catalog/" + product.getProductUrl());
                        result.add(product);
                    }
                }
            }
            Collections.shuffle(result);
            return result;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
    }

    public void postReview(Principal principal, ProductDTO.GetProductReview product) throws BaseException {
        Optional<UserEntity> optional = this.userRepository.findByEmail(principal.getName());
        if(optional.isEmpty()){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if(product.getProductId() == null){
            throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
        }
        if(product.getReview() == null){
            throw new BaseException(BaseResponseStatus.POST_REVEIW_EMPTY);
        }
        ProductEntity productEntity = this.productRepository.findByProductId(product.getProductId());
        if(productEntity == null){
            ProductEntity newProduct = ProductEntity.builder()
                    .productId(product.getProductId())
                    .productUrl(product.getProductUrl())
                    .name(product.getName())
                    .brand(product.getBrand())
                    .type(product.getType())
                    .image(product.getImage())
                    .lowestprice(product.getLowestprice())
                    .date(product.getDate())
                    .description(product.getDescription())
                    .build();
            this.productRepository.save(newProduct);
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(newProduct)
                    .review(product.getReview())
                    .reviewImgUrl(product.getReviewImgUrl())
                    .build();
            this.reviewRepository.save(review);
        }else{
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(productEntity)
                    .review(product.getReview())
                    .reviewImgUrl(product.getReviewImgUrl())
                    .build();
            this.reviewRepository.save(review);
        }
    }

    public void postLike(Principal principal, ProductDTO.PostsetLike product) throws BaseException {
        if(!(this.userRepository.existsByEmail(principal.getName()))){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if(product.getProductId() == null){
            throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
        }
        if(product.getType() == null){
            throw new BaseException(BaseResponseStatus.POST_TYPE_EMPTY);
        }
        if(product.getDetail() == null){
            throw new BaseException(BaseResponseStatus.POST_DETAIL_EMPTY);
        }
        ProductEntity productEntity = this.productRepository.findByProductId(product.getProductId());
        if(productEntity == null){
            productEntity = ProductEntity.builder()
                    .productId(product.getProductId())
                    .productUrl(product.getProductUrl())
                    .type(product.getType())
                    .name(product.getName())
                    .brand(product.getBrand())
                    .image(product.getImage())
                    .date(product.getDate())
                    .description(product.getDescription())
                    .lowestprice(product.getLowestprice())
                    .detail(product.getDetail())
                    .build();
            this.productRepository.save(productEntity);
        }
        UserProductEntity like = UserProductEntity.builder()
                .userEntity(this.userRepository.findByEmail(principal.getName()).get())
                .productEntity(productEntity)
                .build();
        this.userProductRepository.save(like);
    }
}
