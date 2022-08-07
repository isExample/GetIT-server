package com.example.getIt.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class NaverSearchAPI {
    private String apiUrl;
    private String clientId;
    private String clientSecret;
    private int display;

    public NaverSearchAPI(String clientId, String clientSecret){
        this.apiUrl = "https://openapi.naver.com/v1/search/shop.json?";
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.display = 50;
    }
    public void setDisplay(int display){
        this.display = display;
    }
    public JSONObject searchResult(String query){
        String url = this.apiUrl+"display="+this.display+"&query="+query;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        String body = "";
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        JSONObject rjson = new JSONObject(responseEntity.getBody());
        return rjson;
    }

    public JSONArray itemsList(String query){
        JSONObject rjson = this.searchResult(query);
        JSONArray items = rjson.getJSONArray("items");
        return items;
    }
    public JSONObject itemInfo(String productId){
        return null;
    }
}

