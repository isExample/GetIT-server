package com.example.getIt.product.controller;

import com.example.getIt.product.DTO.ItemDTO;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.product.service.NaverShopSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final NaverShopSearch naverShopSearch;

    // 네이버 쇼핑 API에서 데이터를 받아옴
    @GetMapping("/api/search")
    public BaseResponse<List<ItemDTO>> getItems(@RequestParam String query){
        try{
            String resultString = naverShopSearch.search(query);
            return new BaseResponse<>(naverShopSearch.fromJSONtoItems(resultString));
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }
}