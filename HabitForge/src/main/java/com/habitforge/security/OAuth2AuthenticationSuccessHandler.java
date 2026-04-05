package com.habitforge.security;

import com.habitforge.dto.AuthResponse;
import com.habitforge.dto.UserDto;
import com.habitforge.entity.User;
import com.habitforge.service.UserService;
import com.habitforge.service.UserProgressService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserProgressService userProgressService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found after OAuth2 authentication");
        }
        
        // Initialize or update user progress
        userProgressService.updateOrCreateUserProgress(user);
        
        // Generate JWT token
        String token = jwtUtil.generateTokenFromUsername(email);
        
        // Create response
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(UserDto.fromEntity(user))
                .build();
        
        // Set response headers and body
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = String.format(
            "{\"token\":\"%s\",\"type\":\"Bearer\",\"user\":{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"provider\":\"%s\"}}",
            token,
            user.getId(),
            user.getName().replace("\"", "\\\""),
            user.getEmail(),
            user.getProvider().name()
        );
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        
        log.info("OAuth2 authentication successful for user: {}", email);
        
        clearAuthenticationAttributes(request);
    }
}
