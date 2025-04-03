package com.utd.it.soa.esb_service.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter

public class Client {

    private String username;
    private String email;
    private String phone;
    private String lastName;
    private LocalDate birthday;
    private String address;

}