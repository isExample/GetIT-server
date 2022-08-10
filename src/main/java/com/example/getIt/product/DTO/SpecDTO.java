package com.example.getIt.product.DTO;

import lombok.*;

public class SpecDTO {


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FindSpec {
        private String type;
        private String foruse;
        private String forprice;
        private String job;
        private String productImg;
        private String productName;
        private String brand;
        private String productId;
        private String price;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GetSpec {
        private String productImg;
        private String productName;
        private String brand;
        private String productId;
        private String price;
    }
    }

