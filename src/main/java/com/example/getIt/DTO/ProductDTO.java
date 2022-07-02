package com.example.getIt.DTO;

import com.example.getIt.entity.ProductEntity;
import lombok.*;

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
}
