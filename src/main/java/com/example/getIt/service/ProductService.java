package com.example.getIt.service;


import com.example.getIt.DTO.ProductDTO;
import com.example.getIt.DTO.WebsiteDTO;
import com.example.getIt.entity.ProductEntity;
import com.example.getIt.entity.WebsiteEntity;
import com.example.getIt.repository.ProductRepository;
import com.example.getIt.repository.UserRepository;
import com.example.getIt.repository.WebsiteRepository;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponseStatus;
import com.example.getIt.util.JwtService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private WebsiteRepository websiteRepository;

    public ProductService(ProductRepository productRepository, WebsiteRepository websiteRepository){
        this.productRepository = productRepository;
        this.websiteRepository = websiteRepository;
    }
    public List<ProductDTO.GetProduct> getProductAll() throws BaseException {
        List<ProductDTO.GetProduct> getProducts = this.productRepository.findByOrderByCreatedAt();
        return getProducts;
    }

    public ProductDTO.GetProductRes getProduct(Long productIdx) throws BaseException {
        try{
            System.out.println("productEntity");

            ProductEntity productEntity = productRepository.findAllByProductIdx(productIdx);

            System.out.println("websiteEntity");
            List<WebsiteEntity> websiteEntity = websiteRepository.findAllByProduct(productEntity);
            List<WebsiteDTO.GetWebsiteRes> websites = new ArrayList<>();

            for(WebsiteEntity temp : websiteEntity){
                websites.add(new WebsiteDTO.GetWebsiteRes(
                        temp.getWebIdx(),
                        temp.getCost(),
                        temp.getUrl()
                ));
            }

            return new ProductDTO.GetProductRes(
                    productEntity.getProductIdx(),
                    productEntity.getType(),
                    productEntity.getImage(),
                    productEntity.getName(),
                    productEntity.getBrand(),
                    productEntity.getDate(),
                    productEntity.getCpu(),
                    productEntity.getRam(),
                    productEntity.getLowestprice(),
                    productEntity.getDescription(),
                    websites
            );
        }catch (Exception e){
            System.out.println("Error: "+e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
