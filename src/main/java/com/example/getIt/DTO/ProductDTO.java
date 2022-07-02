package com.example.getIt.DTO;

import com.example.getIt.entity.ProductEntity;
import lombok.*;

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

}
