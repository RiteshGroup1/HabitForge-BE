package com.habitforge.security;

import com.habitforge.entity.User;
import com.habitforge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserService userService;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + ex.getMessage());
        }
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        String providerId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }
        
        User user = userService.findByProviderAndProviderId(User.Provider.GOOGLE, providerId);
        
        if (user == null) {
            user = registerNewUser(email, name, providerId);
        } else {
            user = updateExistingUser(user, name);
        }
        
        return UserPrincipal.create(user);
    }
    
    private User registerNewUser(String email, String name, String providerId) {
        log.info("Registering new user with email: {}", email);
        
        if (userService.existsByEmail(email)) {
            throw new RuntimeException("Email already exists with different provider");
        }
        
        User newUser = User.builder()
                .name(name != null ? name : email.split("@")[0])
                .email(email)
                .provider(User.Provider.GOOGLE)
                .providerId(providerId)
                .build();
        
        return userService.saveUser(newUser);
    }
    
    private User updateExistingUser(User user, String name) {
        log.info("Updating existing user with email: {}", user.getEmail());
        
        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
            return userService.saveUser(user);
        }
        
        return user;
    }
}
