package com.example.getIt.product.DTO;

import lombok.*;

@NoArgsConstructor
public class WebsiteDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetWebsiteRes {
        private Long webIdx;
        private String cost;
        private String url;


    }

}
