package com.example.getIt.product.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.json.JSONObject;

import java.util.List;

@NoArgsConstructor
public class ProductDTO {

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetProduct {
        private Long productIdx;
        private String name;
        private String brand;
        private String type;
        private String image;
        private String lowestprice;
        private String productId;
        private String productUrl;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class GetProductReview {
        private String name;
        private String brand;
        private String type;
        private String image;
        private String detail;
        private String lowestprice;
        private String productId;
        private String productUrl;
        private String review;
        private String reviewImgUrl;
        private String date;
        private String description;
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

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetCategoryRes {
        private String type;
        private String requirement;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GetProductList {
        private String imgUrl;
        private String name;
        private String productUrl;
        private String productId;

        public GetProductList(JSONObject jsonObject) {
            this.imgUrl = jsonObject.getString("image");
            this.name = jsonObject.getString("title");
            this.productUrl = jsonObject.getString("productId");
            this.productId = this.productUrl;
        }
    }
    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostsetLike {
        private String productId;
        private String productUrl;
        private String type;
        private String name;
        private String brand;
        private String image;
        private String date;
        private String description;
        private String lowestprice;
        private String detail;
    }

    @Data
    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GetDetail {
        private String name;
        private String cpu;
        private String cpurate;
        private String core;
        private String size;
        private String ram;
        private String weight;
        private String type;
        private String innermemory; // 내장메모리
        private String communication; // 통신 규격
        private String os; // 운영 체제
        private String ssd;
        private String hdd;
        private String output; // 출력
        private String terminal; // 단자

        public void setName(String name){ this.name = name;}
        public void setCpu(String cpu){ this.cpu = cpu;}
        public void setCpurate(String cpurate){ this.cpurate = cpurate;}
        public void setCore(String core){ this.core = core;}
        public void setSize(String size){ this.size = size;}
        public void setRam(String ram){ this.ram = ram;}
        public void setWeight(String weight){ this.weight = weight;}
        public void setType(String type){ this.type = type;}
        public void setInnermemory(String innermemory){ this.innermemory = innermemory;}
        public void setCommunication(String communication){ this.communication = communication;}
        public void setOs(String os){ this.os = os;}
        public void setSsd(String ssd){ this.ssd = ssd;}
        public void setHdd(String hdd){ this.hdd = hdd;}
        public void setOutput(String output){ this.output = output;}
        public void setTerminal(String terminal){ this.terminal = terminal;}
    }
}
