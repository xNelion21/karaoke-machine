package com.karaokeapp.karaoke_backend.services;

import org.springframework.context.annotation.Lazy;
import com.karaokeapp.karaoke_backend.models.Role;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");

        // --- KLUCZOWA LOGIKA ---

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            userRepository.save(user);

        } else {
            User newUser = registerNewOAuth2User(registrationId, email, attributes);
        }

        return oAuth2User;
    }

    private User registerNewOAuth2User(String registrationId, String email, Map<String, Object> attributes) {

        User user = new User();
        user.setEmail(email);
        user.setUsername(email.substring(0, email.indexOf('@')));

        user.setPassword(passwordEncoder.encode("OAUTH2_PASSWORD_PLACEHOLDER_" + registrationId));

        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
    }
}