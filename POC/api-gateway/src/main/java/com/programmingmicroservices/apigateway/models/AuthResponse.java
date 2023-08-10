package com.programmingmicroservices.apigateway.models;

import lombok.*;

import java.util.Collection;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String userId;

    private String accessToken;

    private long expiresAt;

    private String refreshToken;

    private String email;

    private Collection<String> authorities;
}
