// Product.java
package com.utd.it.soa.esb_service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Data
@Getter
@Setter

public class Product {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private Boolean status;
    private String brand;
    private String model;
    private Integer year;
    private String color;

}