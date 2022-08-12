package com.example.getIt.product.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "search")
public class SearchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long searchIdx;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Column
    private Integer countSearch;

    @Builder
    public SearchEntity(String keyword, Integer countSearch){
        this.keyword = keyword;
        this.countSearch = countSearch;
    }

    public void addSearch(){
        this.countSearch += 1;
    }

}
