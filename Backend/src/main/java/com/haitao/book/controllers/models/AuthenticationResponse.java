package com.haitao.book.controllers.models;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    //jwt token
    private String token;

}
