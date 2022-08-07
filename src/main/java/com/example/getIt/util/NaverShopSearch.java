package com.example.getIt.util;

import com.example.getIt.product.DTO.ItemDTO;
import com.example.getIt.product.DTO.ProductDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class NaverShopSearch {
    private static String clientId;
    private static String clientSecret;
    public NaverShopSearch(){}
    public NaverShopSearch(String clientId, String clientSecret){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    public static String search(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        // 넘겨받은 query로 검색 요청
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?query="+query, HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("Response status: " + status);
        System.out.println(response);

        return response;
    }
    public static String specSearch(String type, String use, String maxexpense, String minexpense) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        String body = "";
        String query = type;
        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        String url = "https://openapi.naver.com/v1/search/shop.json?display=3&query=" + query;
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("Response status: " + status);
        System.out.println(response);

        return response;
    }

    public List<ItemDTO> fromJSONtoItems(String result) {
        JSONObject rjson = new JSONObject(result);
        JSONArray items = rjson.getJSONArray("items");
        List<ItemDTO> itemDtoList = new ArrayList<>();
        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = (JSONObject) items.get(i);
            ItemDTO itemDto = new ItemDTO(itemJson);
            itemDtoList.add(itemDto);

        }
        return itemDtoList;
    }
    public List<ProductDTO.GetSpecResultList> fromJSONtoItemsSpec(String result) {
        JSONObject rjson = new JSONObject(result);
        JSONArray items = rjson.getJSONArray("items");
        List<ProductDTO.GetSpecResultList> ResultList = new ArrayList<>();
        for (int i=0; i<items.length(); i++) {
            JSONObject itemJson = (JSONObject) items.get(i);
            ProductDTO.GetSpecResultList spec = new ProductDTO.GetSpecResultList(itemJson);
            ResultList.add(spec);
        }
        return ResultList;
    }
}
