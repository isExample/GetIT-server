package com.example.getIt.product.service;


import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.DTO.WebsiteDTO;
import com.example.getIt.product.entity.ProductEntity;
import com.example.getIt.product.entity.WebsiteEntity;
import com.example.getIt.product.repository.ProductRepository;
import com.example.getIt.product.repository.WebsiteRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;

import org.json.JSONArray;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WebsiteRepository websiteRepository;
    private static final String clientId = "YOUR_CLIENT_ID";
    private static final String clientSecret = "YOUR_CLIENT_SECRET";

    public ProductService(ProductRepository productRepository, WebsiteRepository websiteRepository){
        this.productRepository = productRepository;
        this.websiteRepository = websiteRepository;
    }
    public List<ProductDTO.GetProduct> getProductAll() throws BaseException {
        List<ProductDTO.GetProduct> getProducts = this.productRepository.findByOrderByCreatedAt();
        return getProducts;
    }

    public ProductDTO.GetProductRes getProduct(Long productIdx) throws BaseException {
        try{
            ProductEntity productEntity = productRepository.findAllByProductIdx(productIdx);
            List<WebsiteEntity> websiteEntity = websiteRepository.findAllByProduct(productEntity);
            List<WebsiteDTO.GetWebsiteRes> websites = new ArrayList<>();

            for(WebsiteEntity temp : websiteEntity){
                websites.add(new WebsiteDTO.GetWebsiteRes(
                        temp.getWebIdx(),
                        temp.getCost(),
                        temp.getUrl()
                ));
            }

            return new ProductDTO.GetProductRes(
                    productEntity.getProductIdx(),
                    productEntity.getType(),
                    productEntity.getImage(),
                    productEntity.getName(),
                    productEntity.getBrand(),
                    productEntity.getDate(),
                    productEntity.getCpu(),
                    productEntity.getRam(),
                    productEntity.getLowestprice(),
                    productEntity.getDescription(),
                    websites
            );
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
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
}
