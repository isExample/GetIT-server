package com.example.getIt.entity;

import com.example.getIt.util.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "Product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productIdx;

    @Column(name = "type", nullable = false, length = 10)
    private String type;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "brand", nullable = false, length = 20)
    private String brand;

    @Column(name = "image", nullable = false, length = 50)
    private String image;

    @Column(name = "date", nullable = false, length = 10)
    private String date;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "lowestprice", length = 20)
    private String lowestprice;

    @ManyToOne
    @JoinColumn(name = "webIdx")
    private WebsiteEntity website;

}
