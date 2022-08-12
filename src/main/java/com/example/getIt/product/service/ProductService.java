package com.example.getIt.product.service;


import com.example.getIt.config.Signatures;
import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.DTO.SpecDTO;
import com.example.getIt.product.entity.*;
import com.example.getIt.product.repository.*;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
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
    private static SpecRepository specRepository;
    private String clientId;
    private String clientSecret;
    private ReviewRepository reviewRepository;
    private UserProductRepository userProductRepository;
    private NaverSearchAPI naverSearchAPI;
    private String recommend_accessKey;
    private String recommend_secretKey;
    private String recommend_customerId;
    private S3Uploader s3Uploader;
    private SearchRepository searchRepository;

    public ProductService(ProductRepository productRepository, WebsiteRepository websiteRepository, UserRepository userRepository,
                          SpecRepository specRepository, ReviewRepository reviewRepository, UserProductRepository userProductRepository, @Value("${clientId}") String clientId, @Value("${clientSecret}") String clientSecret,
                          @Value("${recommend.customerId}") String recommend_customerId, @Value("${recommend.accessKey}") String recommend_accessKey,
                          @Value("${recommend.secretKey}") String recommend_secretKey,
                          S3Uploader s3Uploader, SearchRepository searchRepository) {
        this.productRepository = productRepository;
        this.websiteRepository = websiteRepository;
        this.specRepository = specRepository;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.userProductRepository = userProductRepository;
        this.searchRepository = searchRepository;
        this.naverSearchAPI = new NaverSearchAPI(this.clientId, this.clientSecret);
//        this.naverShopSearch = naverShopSearch;
        this.recommend_customerId = recommend_customerId;
        this.recommend_accessKey = recommend_accessKey;
        this.recommend_secretKey = recommend_secretKey;
        this.s3Uploader = s3Uploader;
    }
    public List<ProductDTO.GetProduct> getProductAll() throws BaseException {
        List<ProductDTO.GetProduct> getProducts = this.productRepository.findByOrderByCreatedAt();
        return getProducts;
    }

    public ProductDTO.GetDetail getProduct(String productIdx) throws BaseException, IOException {
        return getProductDetailList(productIdx);
    }

    public List<ProductDTO.GetProductList> getCategoryList(ProductDTO.GetCategoryRes getCategoryRes) throws BaseException {
        try {
            String query = getCategoryRes.getType() + "," + getCategoryRes.getRequirement();
            query = query.replace(",null", "");
            JSONArray items = this.naverSearchAPI.itemsList(query);
            if (items.isEmpty()) {
                throw new Exception();
            } else {
                List<ProductDTO.GetProductList> result = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject eachItem = (JSONObject) items.get(i);
                    ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                    product.setProductUrl("https://search.shopping.naver.com/catalog/" + product.getProductUrl());
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
            for (int i = 0; i < 5; i++) {
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
                if (items.isEmpty()) {
                    throw new Exception();
                } else {
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

    public void postReview(Principal principal, ProductDTO.GetProductReview product, MultipartFile reviewImg) throws BaseException {
        if(principal.equals(null)){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        Optional<UserEntity> optional = this.userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if (product.getProductId() == null) {
            throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
        }
        if (product.getReview() == null) {
            throw new BaseException(BaseResponseStatus.POST_REVEIW_EMPTY);
        }
        String reviewProfileUrl = null;
        if(!reviewImg.isEmpty()){
            try {
                reviewProfileUrl = s3Uploader.upload(reviewImg, "review");
            } catch (IOException e) {
                throw new BaseException(BaseResponseStatus.POST_REVIEW_IMG_ERROR);
            }
        }
        ProductEntity productEntity = this.productRepository.findByProductId(product.getProductId());
        if (productEntity == null) {
            ProductEntity newProduct = ProductEntity.builder()
                    .productId(product.getProductId())
                    .productUrl(product.getProductUrl())
                    .name(product.getName())
                    .brand(product.getBrand())
                    .date(product.getCpu())
                    .cpurate(product.getCpurate())
                    .core(product.getCore())
                    .size(product.getSize())
                    .ram(product.getRam())
                    .weight(product.getWeight())
                    .type(product.getType())
                    .innermemory(product.getInnermemory())
                    .communication(product.getCommunication())
                    .os(product.getOs())
                    .ssd(product.getSsd())
                    .hdd(product.getHdd())
                    .output(product.getOutput())
                    .terminal(product.getTerminal())
                    .build();
            this.productRepository.save(newProduct);
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(newProduct)
                    .review(product.getReview())
                    .reviewImgUrl(reviewProfileUrl)
                    .build();
            this.reviewRepository.save(review);
        } else {
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(productEntity)
                    .review(product.getReview())
                    .reviewImgUrl(reviewProfileUrl)
                    .build();
            this.reviewRepository.save(review);
        }
    }

    public String postLike(Principal principal, ProductDTO.PostsetLike product) throws BaseException {
        try {
            if (!(this.userRepository.existsByEmail(principal.getName()))) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }
            if (product.getProductId() == null) {
                throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
            }
            if (product.getType() == null) {
                throw new BaseException(BaseResponseStatus.POST_TYPE_EMPTY);
            }
            ProductEntity productEntity = this.productRepository.findByProductId(product.getProductId());
            if (productEntity == null) {
                productEntity = ProductEntity.builder()
                        .productId(product.getProductId())
                        .productUrl(product.getProductUrl())
                        .name(product.getName())
                        .brand(product.getBrand())
                        .date(product.getCpu())
                        .cpurate(product.getCpurate())
                        .core(product.getCore())
                        .size(product.getSize())
                        .ram(product.getRam())
                        .weight(product.getWeight())
                        .type(product.getType())
                        .innermemory(product.getInnermemory())
                        .communication(product.getCommunication())
                        .os(product.getOs())
                        .ssd(product.getSsd())
                        .hdd(product.getHdd())
                        .output(product.getOutput())
                        .terminal(product.getTerminal())
                        .build();
                this.productRepository.save(productEntity);
            }
            UserEntity userEntity = this.userRepository.findByEmail(principal.getName()).get();
            UserProductEntity userProductEntity = this.userProductRepository.findAllByUserIdxAndProductIdx(userEntity, productEntity);
            if(userProductEntity == null){
                UserProductEntity like = UserProductEntity.builder()
                        .userEntity(userEntity)
                        .productEntity(productEntity)
                        .build();
                this.userProductRepository.save(like);
                return "좋아요 목록에 추가되었습니다.";
            }else{
                this.userProductRepository.deleteByUserProductIdx(userProductEntity.getUserProductIdx());
                return "좋아요를 취소하였습니다.";
            }

        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ProductDTO.GetDetail getProductDetailList(String productIdx) throws BaseException{
        final String url = "https://search.shopping.naver.com/catalog/" + productIdx;
        try {
            Document doc = Jsoup.connect(url).get();
            return getProductDetailList(doc);
        } catch (IOException ignored) {
        }
        return null;
    }

    public ProductDTO.GetDetail getProductDetailList(Document doc) throws BaseException{
        Elements namecontents = doc.select("div.top_summary_title__15yAr > h2");
        Elements comAndDatecontents = doc.select("div.top_info_inner__1cEYE > span");
        Elements contents = doc.select("div.top_summary_title__15yAr > div:nth-child(4) >span");
        String[] productinfo = new String[contents.size()];
        String[] content = new String[contents.size()];
        List<ProductDTO.GetDetail> DetailDTO = new ArrayList<>();
        ProductDTO.GetDetail productDetail = new ProductDTO.GetDetail();
        productDetail.setName(namecontents.text());
        productDetail.setBrand(comAndDatecontents.get(1).text().substring(4));
        productDetail.setDate(comAndDatecontents.get(3).text().substring(4));
        for (int i = 0; i < contents.size(); i++) {
            productinfo[i] = contents.get(i).text();
            content[i] = productinfo[i].substring(productinfo[i].lastIndexOf(":")+2);
        }
        for (int j = 0; j < content.length; j++) {
           if (productinfo[j].contains("스마트폰")) {
                if (productinfo[j].contains("CPU속도")) {
                   productDetail.setCpurate(content[j]);
                   DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어i")||productinfo[j].contains("M1")||productinfo[j].contains("M2")||productinfo[j].contains("셀러론")||productinfo[j].contains("라이젠")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")){
                    productDetail.setCore(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("크기")){
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("램")){
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("무게")){
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("품목")){
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if(productinfo[j].contains("내장메모리")){
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("통신규격")){
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("운영체제")){
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }

            } else if (productinfo[j].contains("데스크탑")) {
               if (productinfo[j].contains("CPU속도")) {
                   productDetail.setCpurate(content[j]);
                   DetailDTO.add(productDetail);
               }
                if (productinfo[j].contains("코어i")||productinfo[j].contains("M1")||productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")){
                   productDetail.setCore(content[j]);
                   DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("크기")){
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("램")){
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("무게")){
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("품목")){
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if(productinfo[j].contains("SSD")){
                    productDetail.setSsd(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("HDD")){
                    productDetail.setHdd(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else if (productinfo[j].contains("패드")) {
               if (productinfo[j].contains("CPU속도")) {
                   productDetail.setCpurate(content[j]);
                   DetailDTO.add(productDetail);
               }
                if (productinfo[j].contains("코어i")||productinfo[j].contains("M1")||productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")){
                   productDetail.setCore(content[j]);
                   DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("크기")){
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("램")){
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("무게")){
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("품목")){
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if(productinfo[j].contains("내장메모리")){
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("통신규격")){
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("운영체제")){
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else if (productinfo[j].contains("스피커")) {
                if(productinfo[j].contains("품목")){
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("출력")){
                    productDetail.setOutput(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("단자")){
                    productDetail.setTerminal(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("무게")){
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else {
               if (productinfo[j].contains("CPU속도")) {
                   productDetail.setCpurate(content[j]);
                   DetailDTO.add(productDetail);
               }
                if (productinfo[j].contains("코어i")||productinfo[j].contains("M1")||productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")){
                   productDetail.setCore(content[j]);
                   DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("크기")){
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("램")){
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("무게")){
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("품목")){
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if(productinfo[j].contains("내장메모리")){
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("통신규격")){
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("운영체제")){
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("내장메모리")){
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("통신규격")){
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if(productinfo[j].contains("운영체제")){
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
            }
        }
        return productDetail;
    }

    public List<ProductDTO.ReviewList> getReviewList(String productIdx) throws BaseException {
        try{
            ProductEntity productEntity = productRepository.findAllByProductIdx(Long.parseLong(productIdx));
            List<ReviewEntity> reviewEntity = reviewRepository.findAllByProductIdx(productEntity);
            List<ProductDTO.ReviewList> reviewList = new ArrayList<>();

            for(ReviewEntity i : reviewEntity){
                ProductDTO.ReviewList review = new ProductDTO.ReviewList();
                review.setReviewIdx(i.getReviewIdx());
                review.setProductIdx(i.getProductIdx().getProductIdx());
                review.setNickName(i.getUserIdx().getNickname());
                review.setReview(i.getReview());
                review.setReviewImgUrl(i.getReviewImgUrl());
                reviewList.add(review);
            }
            return reviewList;
        }catch(Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
    }

    public List<ProductDTO.GetProductList> getRecommProducts(String topic) throws BaseException{
        try {
            // Search
            this.naverSearchAPI.setDisplay(20);
            JSONArray items = this.naverSearchAPI.itemsList(topic);
            // Create Random Number(1~20)
            List<Integer> randomNum = new ArrayList<>();
            for(int i=1; i<20; i++){
                randomNum.add(i);
            }
            Collections.shuffle(randomNum);
            // Get Random Product
            List<ProductDTO.GetProductList> RecommProducts = new ArrayList<>();
            for(int i=0; i<6; i++){
                JSONObject eachItem = (JSONObject) items.get(randomNum.get(i));
                ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                product.setProductUrl("https://search.shopping.naver.com/catalog/" + product.getProductUrl());
                RecommProducts.add(product);
            }
            return RecommProducts;
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
    }

    public List<ProductDTO.Recommend> recommend() throws BaseException {
        List<SearchEntity> searchEntities = this.searchRepository.findByOrderByCountSearchDesc();
        List<ProductDTO.Recommend> recommends = new ArrayList<>();

        if(searchEntities.size()<10){
            for(int i = 0; i<searchEntities.size(); i++){
                recommends.add(new ProductDTO.Recommend(searchEntities.get(i).getKeyword()));
            }
        }else{
            for(int i = 0 ; i<10; i++){
                recommends.add(new ProductDTO.Recommend(searchEntities.get(i).getKeyword()));
            }
        }
        return recommends;
    }

    public static List<SpecDTO.GetSpec> getSpecList(SpecDTO.FindSpec specdto) {
        List<SpecEntity> specEntity = specRepository.findAllByTypeAndForuseAndForpriceAndJob(specdto.getType(),specdto.getForuse(), specdto.getForprice(), specdto.getJob());
        List<SpecDTO.GetSpec> specList = new ArrayList<>();

        for(SpecEntity i : specEntity){
            SpecDTO.GetSpec spec = new SpecDTO.GetSpec();
            spec.setProductId(i.getProductId());
            spec.setProductImg(i.getProductImg());
            spec.setBrand(i.getBrand());
            spec.setProductName(i.getProductName());
            spec.setPrice(i.getPrice());
            specList.add(spec);
        }
        return specList;
    }
    public SpecEntity specsave(SpecDTO.FindSpec spec){
        SpecEntity specEntity = SpecEntity.builder()
                .type(spec.getType())
                .foruse(spec.getForuse())
                .forprice(spec.getForprice())
                .job(spec.getJob())
                .productImg(spec.getProductImg())
                .productName(spec.getProductName())
                .brand(spec.getBrand())
                .price(spec.getPrice())
                .productId(spec.getProductId())
                .build();
        specRepository.save(specEntity);
        return specEntity;
    }

    public void deleteReview(Principal principal, Long reviewIdx) throws BaseException {
        if(principal.equals(null)){
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        ReviewEntity reviewEntity = reviewRepository.findByReviewIdx(reviewIdx);
        Optional<UserEntity> optional = this.userRepository.findByEmail(principal.getName());

        if(reviewEntity != null) {
            if(optional.isPresent()) {
                UserEntity userEntity = optional.get();
                if(userEntity.getUserIdx().equals(reviewEntity.getUserIdx().getUserIdx())){
                    userEntity.getReviews().remove(reviewEntity);
                    reviewRepository.deleteById(reviewIdx);
                }
                else{
                    throw new BaseException(BaseResponseStatus.INCORRECT_USER);
                }
            } else{
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }
//            if(reviewEntity.getReviewImgUrl() != null) {
//                String fileName = reviewEntity.getReviewImgUrl();
//                s3Uploader.delete(fileName);
//            }
        }
        else{
            throw new BaseException(BaseResponseStatus.UNEXIST_REVIEW);
        }
    }
}
