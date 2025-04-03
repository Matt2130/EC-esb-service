package com.utd.it.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Order {

    private LocalDateTime orderDate;
    private BigDecimal total;
    private Boolean status = true;
    private String paymentMethod;
    private String paymentStatus = "pendiente";
    private String deliveryAddress;
    private LocalDate deliveryDate;

}