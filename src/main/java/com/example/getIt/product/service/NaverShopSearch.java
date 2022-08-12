package com.example.getIt.product.service;

import com.example.getIt.product.DTO.ItemDTO;
import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.product.entity.SearchEntity;
import com.example.getIt.product.repository.SearchRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
//@Component
public class NaverShopSearch {
    private String clientId;
    private String clientSecret;
    private SearchRepository searchRepository;

    public NaverShopSearch(@Value("${clientId}") String clientId, @Value("${clientSecret}") String clientSecret, SearchRepository searchRepository){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.searchRepository = searchRepository;
    }
    public String search(String query)  throws BaseException {
        if(query.equals(null)){
            throw new BaseException(BaseResponseStatus.FAILED_TO_SEARCH);
        }
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", this.clientId);
        headers.add("X-Naver-Client-Secret", this.clientSecret);
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        // 넘겨받은 query로 검색 요청
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?query="+query, HttpMethod.GET, requestEntity, String.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("Response status: " + status);
        System.out.println(response);

        SearchEntity search = searchRepository.findByKeyword(query);
        if(search == null){
            search = SearchEntity.builder()
                    .keyword(query)
                    .countSearch(1)
                    .build();
            this.searchRepository.save(search);

        }else{
            search.addSearch();
            this.searchRepository.save(search);
        }
        return response;
    }
    public String specSearch(String type, String use, String maxexpense, String minexpense) {
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
