package com.example.getIt.product.DTO;

import lombok.*;
import net.minidev.json.JSONObject;

import java.util.List;

@NoArgsConstructor
public class ProductDTO {

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetProduct{
        private Long productIdx;
        private String name;
        private String brand;
        private String type;
        private String image;
        private String lowestprice;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductRes {
        private Long productIdx;
        private String type;
        private String image;
        private String name;
        private String brand;
        private String date;
        private String cpu;
        private String ram;
        private String price;
        private String description;
        private List<WebsiteDTO.GetWebsiteRes> websites;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetCategoryRes{
        private String type;
        private String brand;
        private String cpu;
        private String ram;
        private String screenSize;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductList{
        private String imgUrl;
        private String name;

        public GetProductList(JSONObject jsonObject){
            this.imgUrl = jsonObject.getAsString("image");
            this.name = jsonObject.getAsString("title");
        }
    }
}
