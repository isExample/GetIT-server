package com.example.getIt.product.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.json.JSONObject;

@NoArgsConstructor
public class ProductDTO {

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetProduct {
        private Long productIdx;
        private String name;
        private String brand;
        private String type;
        private String image;
        private String lowestprice;
        private String productId;
        private String productUrl;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetProductReview {
        private String name;
        private String brand;
        private String date;
        private String cpu;
        private String cpurate;
        private String core;
        private String size;
        private String ram;
        private String weight;
        private String type;
        private String innermemory; // 내장메모리
        private String communication; // 통신 규격
        private String os; // 운영 체제
        private String ssd;
        private String hdd;
        private String output; // 출력
        private String terminal; // 단자
        private String productId;
        private String productUrl;
        private String review;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetCategoryRes {
        private String type;
        private String requirement;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductList {
        private String imgUrl;
        private String name;
        private String productUrl;
        private String productId;

        public GetProductList(JSONObject jsonObject) {
            this.imgUrl = jsonObject.getString("image");
            this.name = jsonObject.getString("title");
            this.productUrl = jsonObject.getString("productId");
            this.productId = this.productUrl;
        }
    }
    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostsetLike {
        private String productId;
        private String productUrl;
        private String name;
        private String brand;
        private String date;
        private String cpu;
        private String cpurate;
        private String core;
        private String size;
        private String ram;
        private String weight;
        private String type;
        private String innermemory; // 내장메모리
        private String communication; // 통신 규격
        private String os; // 운영 체제
        private String ssd;
        private String hdd;
        private String output; // 출력
        private String terminal; // 단자
    }

    @Data
    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetDetail {
        private String name;
        private String brand;
        private String date;
        private String cpu;
        private String cpurate;
        private String core;
        private String size;
        private String ram;
        private String weight;
        private String type;
        private String innermemory; // 내장메모리
        private String communication; // 통신 규격
        private String os; // 운영 체제
        private String ssd;
        private String hdd;
        private String output; // 출력
        private String terminal; // 단자
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewList{
        private Long reviewIdx;
        private Long productIdx;
        private String nickName;
        private String review;
        private String reviewImgUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Recommend{
        private String keyword;

    }
    @Getter
    @NoArgsConstructor
    public static class GetSpecResultList {
        private String title;
        private String link;
        private String image;
        private String lprice;
        private String brand;


        public GetSpecResultList(JSONObject itemJson) {
            this.title = itemJson.getString("title");
            this.link = itemJson.getString("link");
            this.image = itemJson.getString("image");
            this.lprice = itemJson.getString("lprice");
            this.brand = itemJson.getString("maker");
        }
    }

}
