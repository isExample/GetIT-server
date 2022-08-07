package com.example.getIt.product.DTO;

import lombok.*;

public class SpecDTO {


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FindSpec {
        private String type; // 기기 종류
        private String purpose; // 용도
        private String Minexpense; // 최소경비
        private String Maxexpense; //최대경비
        private String job; // 직업

        public String getType() {
            return type;
        }

        public String getPurpose() {
            return purpose;
        }

        public String getMinexpense() {
            return Minexpense;
        }
        public String getMaxexpense() {
            return Maxexpense;
        }
        public String getJob() {
            return job;
        }
    }
}
