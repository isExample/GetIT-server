package com.example.getIt.product.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@NoArgsConstructor
@Getter
public class ItemDTO {
    private String title; // 이름
    private String productId;
    private String image;
    private int lprice;

    public ItemDTO(JSONObject itemJson) {
        this.title = itemJson.getString("title");
        this.productId = itemJson.getString("productId");
        this.image = itemJson.getString("image");
        this.lprice = itemJson.getInt("lprice");
    }
}