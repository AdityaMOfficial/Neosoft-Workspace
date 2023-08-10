package com.programmingmicroservices.apigateway.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import com.programmingmicroservices.apigateway.models.AuthResponse;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {


    private ObjectMapper objectMapper = new ObjectMapper();


    //@AuthenticationPrincipal OidcUser user,
    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RegisteredOAuth2AuthorizedClient("my-client") OAuth2AuthorizedClient oAuth2AuthorizedClient,
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model
    ) {
        log.info("Email id :: {}",oidcUser.getEmail());
        log.info("Principal name {}", oAuth2AuthorizedClient.getPrincipalName());
        log.info("Client registration {}", oAuth2AuthorizedClient.getClientRegistration());

        System.out.println(oAuth2AuthorizedClient.getAccessToken().getScopes());

//        if (user != null){
//            OidcIdToken token = user.getIdToken();
//
//        Map<String, Object> customClaims = token.getClaims();
//
//        if (customClaims.containsKey("user_id")) {
//            userId = String.valueOf(customClaims.get("user_id"));
//        }
//
//        model.addAttribute("username", user.getName());
//        model.addAttribute("userID", userId);
//        log.info("userId :: {}",userId);
//        log.info("user.getName() :: {}",user.getName());
//        log.info("Model :: {}",model);
//    }
//        String[] tokenSplit = oAuth2AuthorizedClient.getAccessToken().getTokenValue().split("\\.");
//        byte[] byteValueBase64Decoded = Base64.getUrlDecoder().decode(tokenSplit[1]);
//        String stringValueBase64Decoded = new String(byteValueBase64Decoded);
//        System.out.println(" when decoded is: " + stringValueBase64Decoded);
//        Map<String,String> user = null;
//        try {
//            user = objectMapper.readValue(stringValueBase64Decoded, Map.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("User"+user);
        System.out.println("Oidc user"+oidcUser);

//        ((List<String>)((Map<String,List<String>>)oidcUser.getClaims().get("realm_access")).get("roles")).stream().forEach(System.out::println);
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(oidcUser,null);


        return new ResponseEntity<>(AuthResponse.builder()
                .accessToken(oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                .refreshToken(oAuth2AuthorizedClient.getRefreshToken().getTokenValue())
                .userId(oidcUser.getPreferredUsername())
                .email(oidcUser.getEmail())
                .expiresAt(oAuth2AuthorizedClient.getAccessToken().getExpiresAt().getEpochSecond())
                .authorities(oidcUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }
}
