package com.example.getIt.product.service;


import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.DTO.SpecDTO;
import com.example.getIt.product.entity.*;
import com.example.getIt.product.repository.*;
import com.example.getIt.user.entity.UserEntity;
import com.example.getIt.user.repository.UserRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
import com.example.getIt.util.NaverSearchAPI;
import com.example.getIt.util.S3Uploader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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

    public ProductDTO.GetProductItemList getCategoryList(String type, String requirement) throws BaseException {
        try {
            String query = type + "," + requirement;
            query = query.replace(",null", "");
            JSONArray items = this.naverSearchAPI.itemsList(query);
            if (items.isEmpty()) {
                throw new Exception();
            } else {
                List<ProductDTO.GetProductList> result = new ArrayList<>();
                for (int i = 0; i < items.length(); i++) {
                    JSONObject eachItem = (JSONObject) items.get(i);
                    ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                    product.setName(product.getName().replace("<b>", ""));
                    product.setName(product.getName().replace("</b>", ""));
                    result.add(product);
                }
                ProductDTO.GetProductItemList products = new ProductDTO.GetProductItemList(result);
                return products;
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

    public void postReview(Principal principal, ProductDTO.GetProductReview productReview) throws BaseException {
        if (principal.equals(null)) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        Optional<UserEntity> optional = this.userRepository.findByEmail(principal.getName());
        if (optional.isEmpty()) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }
        if (productReview.getProductId() == null) {
            throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
        }
        if (productReview.getReview() == null) {
            throw new BaseException(BaseResponseStatus.POST_REVEIW_EMPTY);
        }
        ProductEntity productEntity = this.productRepository.findByProductId(productReview.getProductId());
        if (productEntity == null) {
            ProductDTO.GetDetail product = getProductDetailList(productReview.getProductId());
            ProductEntity newProduct = ProductEntity.builder()
                    .productId(product.getProductIdx())
                    .productUrl(product.getLink())
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
                    .image(product.getPhotolist().get(0))
                    .build();
            this.productRepository.save(newProduct);
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(newProduct)
                    .review(productReview.getReview())
                    .build();
            this.reviewRepository.save(review);
        } else {
            ReviewEntity review = ReviewEntity.builder()
                    .userEntity(optional.get())
                    .productEntity(productEntity)
                    .review(productReview.getReview())
                    .build();
            this.reviewRepository.save(review);
        }
    }

    public String postLike(Principal principal, ProductDTO.PostsetLike productId) throws BaseException {
        try {
            if (!(this.userRepository.existsByEmail(principal.getName()))) {
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }
            if (productId.getProductId() == null) {
                throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
            }

            ProductDTO.GetDetail product = getProductDetailList(productId.getProductId());
            JSONArray items = this.naverSearchAPI.itemsList(product.getName());
            String lprice = "null";
            JSONObject eachItem = (JSONObject) items.get(0);
            lprice = eachItem.getString("lprice");

            if (product.getType() == null) {
                throw new BaseException(BaseResponseStatus.POST_TYPE_EMPTY);
            }
            ProductEntity productEntity = this.productRepository.findByProductId(product.getProductIdx());
            if (productEntity == null) {
                productEntity = ProductEntity.builder()
                        .productId(product.getProductIdx())
                        .productUrl(product.getLink())
                        .image(product.getPhotolist().get(0))
                        .name(product.getName())
                        .brand(product.getBrand())
                        .date(product.getCpu())
                        .lowestprice(lprice)
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
            if (userProductEntity == null) {
                UserProductEntity like = UserProductEntity.builder()
                        .userEntity(userEntity)
                        .productEntity(productEntity)
                        .build();
                this.userProductRepository.save(like);
                return "좋아요 목록에 추가되었습니다.";
            } else {
                this.userProductRepository.deleteByUserProductIdx(userProductEntity.getUserProductIdx());
                return "좋아요를 취소하였습니다.";
            }

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public ProductDTO.GetDetail getProductDetailList(String productIdx) throws BaseException {
        final String url = "https://search.shopping.naver.com/catalog/" + productIdx;
        try {
            Document doc = Jsoup.connect(url).get();
            return getProductDetailList(doc);
        } catch (IOException ignored) {
        }
        return null;
    }

    public ProductDTO.GetDetail getProductDetailList(Document doc) throws BaseException, IOException {
        Elements namecontents = doc.select("div.top_summary_title__ViyrM > h2");
        Elements comAndDatecontents = doc.select("div.top_info_inner__aM_0Z > span");
        Elements contents = doc.select("div.top_summary_title__ViyrM > div:nth-child(4) >span");
        Elements link = doc.select("head > meta:nth-child(8)");
        Elements img = doc.select("div.style_content__v25xx > div > div.image_thumb_area__fIhuf");
        Elements imgs = img.select("img");

        String url = link.attr("content");
        if(url.length()<10){
            throw new BaseException(BaseResponseStatus.FAILED_TO_FIND_URL);
        }
        String productIdx = url.substring(42,53);

        String[] productinfo = new String[contents.size()];
        String[] content = new String[contents.size()];
        List<ProductDTO.GetDetail> DetailDTO = new ArrayList<>();
        List<String> photoLists = new ArrayList<>();
        ProductDTO.GetDetail productDetail = new ProductDTO.GetDetail();
        for (Element e : imgs)
            photoLists.add(e.attr("src"));
        productDetail.setName(namecontents.text());
        productDetail.setLink(url);
        productDetail.setProductIdx(productIdx);
        productDetail.setBrand(comAndDatecontents.get(1).text().substring(4));
        productDetail.setDate(comAndDatecontents.get(3).text().substring(4));
        productDetail.setPhotolist(photoLists);


        for (int i = 0; i < contents.size(); i++) {
            productinfo[i] = contents.get(i).text();
            content[i] = productinfo[i].substring(productinfo[i].lastIndexOf(":") + 2);
        }
        for (int j = 0; j < content.length; j++) {
            if (productinfo[j].contains("스마트폰")) {
                if (productinfo[j].contains("CPU속도")) {
                    productDetail.setCpurate(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어i") || productinfo[j].contains("M1") || productinfo[j].contains("M2") || productinfo[j].contains("셀러론") || productinfo[j].contains("라이젠")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")) {
                    productDetail.setCore(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("크기")) {
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("램")) {
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("무게")) {
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("품목")) {
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("내장메모리")) {
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("통신규격")) {
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("운영체제")) {
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else if (productinfo[j].contains("데스크탑")) {
                if (productinfo[j].contains("CPU속도")) {
                    productDetail.setCpurate(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어i") || productinfo[j].contains("M1") || productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")) {
                    productDetail.setCore(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("크기")) {
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("램")) {
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("무게")) {
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("품목")) {
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if (productinfo[j].contains("SSD")) {
                    productDetail.setSsd(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("HDD")) {
                    productDetail.setHdd(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else if (productinfo[j].contains("패드")) {
                if (productinfo[j].contains("CPU속도")) {
                    productDetail.setCpurate(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어i") || productinfo[j].contains("M1") || productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")) {
                    productDetail.setCore(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("크기")) {
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("램")) {
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("무게")) {
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("품목")||productinfo[j].contains("형태")) {
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("내장메모리")) {
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("통신규격")||productinfo[j].contains("인터넷연결")) {
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("운영체제")||productinfo[j].contains("출시OS")) {
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else if (productinfo[j].contains("스피커")||productinfo[j].contains("헤드셋")||productinfo[j].contains("헤드폰")||productinfo[j].contains("사운드바")) {
                if (productinfo[j].contains("품목")||productinfo[j].contains("형태")) {
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("정격출력")||productinfo[j].contains("W")||productinfo[j].contains("출력")) {
                   productDetail.setOutput(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("단자")) {
                    productDetail.setTerminal(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("무게")) {
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
            } else {
                if (productinfo[j].contains("CPU속도")) {
                    productDetail.setCpurate(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어i") || productinfo[j].contains("M1") || productinfo[j].contains("M2")) {
                    productDetail.setCpu(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("코어종류")) {
                    productDetail.setCore(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("크기")) {
                    productDetail.setSize(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("램")) {
                    productDetail.setRam(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("무게")) {
                    productDetail.setWeight(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("품목")) {
                    productDetail.setType(content[j]);
                    DetailDTO.add(productDetail);
                }

                if (productinfo[j].contains("내장메모리")) {
                    productDetail.setInnermemory(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("통신규격")) {
                    productDetail.setCommunication(content[j]);
                    DetailDTO.add(productDetail);
                }
                if (productinfo[j].contains("운영체제")) {
                    productDetail.setOs(content[j]);
                    DetailDTO.add(productDetail);
                }
            }

                if (productDetail.getName() == null) {
                    productDetail.setName("미상");
                }
                if (productDetail.getLink() == null) {
                    productDetail.setLink("미상");
                }
                if (productDetail.getBrand() == null) {
                    productDetail.setBrand("미상");
                }
                if (productDetail.getDate() == null) {
                    productDetail.setDate("미상");
                }
                if (productDetail.getCpu() == null) {
                    productDetail.setCpu("미상");
                }
                if (productDetail.getCpurate() == null) {
                    productDetail.setCpurate("미상");
                }
                if (productDetail.getCore() == null) {
                    productDetail.setCore("미상");
                }
                if (productDetail.getSize() == null) {
                    productDetail.setSize("미상");
                }
                if (productDetail.getRam() == null) {
                    productDetail.setRam("미상");
                }
                if (productDetail.getWeight() == null) {
                    productDetail.setWeight("미상");
                }
                if (productDetail.getType() == null) {
                    productDetail.setType("미상");
                }
                if (productDetail.getInnermemory() == null) {
                    productDetail.setInnermemory("미상");
                }
                if (productDetail.getCommunication() == null) {
                    productDetail.setCommunication("미상");
                }
                if (productDetail.getOs() == null) {
                    productDetail.setOs("미상");
                }
                if (productDetail.getSsd() == null) {
                    productDetail.setSsd("미상");
                }
                if (productDetail.getHdd() == null) {
                    productDetail.setHdd("미상");
                }
                if (productDetail.getOutput() == null) {
                    productDetail.setOutput("미상");
                }
                if (productDetail.getTerminal() == null) {
                    productDetail.setTerminal("미상");
                }
        }
        return productDetail;
    }


    public List<ProductDTO.ReviewList> getReviewList(String productId) throws BaseException {
        try{
            ProductEntity productEntity = productRepository.findByProductId(productId);
            List<ReviewEntity> reviewEntity = reviewRepository.findAllByProductIdx(productEntity);
            List<ProductDTO.ReviewList> reviewList = new ArrayList<>();

            for(ReviewEntity i : reviewEntity){
                ProductDTO.ReviewList review = new ProductDTO.ReviewList();
                review.setReviewIdx(i.getReviewIdx());
                review.setProductId(productId);
                review.setNickName(i.getUserIdx().getNickname());
                review.setReview(i.getReview());
                review.setProfileImgUrl(i.getUserIdx().getProfileImgUrl());
                reviewList.add(review);
            }
            return reviewList;
        }catch(Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
    }

    public ProductDTO.GetRecommItemList getRecommProducts() throws BaseException{
        try {
            List<String> topic = new ArrayList<>();
            topic.add("최신 노트북");
            topic.add("최신 핸드폰");
            topic.add("최신 태블릿 PC");
            topic.add("스피커");
            topic.add("무선 이어폰");
            topic.add("게이밍 PC");
            // Search
            int number = (int) (Math.random()*6);
            this.naverSearchAPI.setDisplay(20);
            JSONArray items = this.naverSearchAPI.itemsList(topic.get(number));
            // Create Random Number(1~20)
            List<Integer> randomNum = new ArrayList<>();
            for(int i=1; i<20; i++){
                randomNum.add(i);
            }
            Collections.shuffle(randomNum);
            // Get Random Product
            List<ProductDTO.GetProductList> recommProducts = new ArrayList<>();
            for(int i=0; i<6; i++){
                JSONObject eachItem = (JSONObject) items.get(randomNum.get(i));
                ProductDTO.GetProductList product = new ProductDTO.GetProductList(eachItem);
                recommProducts.add(product);
            }
            ProductDTO.GetRecommItemList products = new ProductDTO.GetRecommItemList(topic.get(number), recommProducts);
            return products;
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
        List<SpecEntity> specEntity;
        if(specdto.getPlus()==null){
            specEntity = specRepository.findAllByTypeAndForuseAndForprice(specdto.getType(),specdto.getForuse(), specdto.getForprice());
        }
        else{
            specEntity = specRepository.findAllByTypeAndForuseAndForpriceAndPlus(specdto.getType(),specdto.getForuse(), specdto.getForprice(), specdto.getPlus());
        }
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
    /*public SpecEntity specsave(SpecDTO.FindSpec spec){
        SpecEntity specEntity = SpecEntity.builder()
                .type(spec.getType())
                .foruse(spec.getForuse())
                .forprice(spec.getForprice())
                .plus(spec.getPlus())
                .productImg(spec.getProductImg())
                .productName(spec.getProductName())
                .brand(spec.getBrand())
                .price(spec.getPrice())
                .productId(spec.getProductId())
                .build();
        specRepository.save(specEntity);
        return specEntity;
    }*/

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

    public ProductDTO.GetIsLike getProductIsLike(Principal principal, String productId) throws BaseException {
        try{
            if(!(this.userRepository.existsByEmail(principal.getName()))){
                throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
            }
            if(productId.equals(null)){
                throw new BaseException(BaseResponseStatus.POST_PRODUCTID_EMPTY);
            }

            ProductDTO.GetIsLike result = new ProductDTO.GetIsLike();
            result.setIsLike(false);
            if(this.userProductRepository.existsByUserIdxAndProductIdx(this.userRepository.findByEmail(principal.getName()).get(),
                    this.productRepository.findByProductId(productId))){
                result.setIsLike(true);
            }

            return result;
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
