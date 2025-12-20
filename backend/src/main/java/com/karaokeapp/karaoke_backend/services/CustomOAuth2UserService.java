package com.karaokeapp.karaoke_backend.services;

import com.karaokeapp.karaoke_backend.models.Role;
import com.karaokeapp.karaoke_backend.models.User;
import com.karaokeapp.karaoke_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Pobranie podstawowych danych z Facebooka
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 2. Wyciągnięcie e-maila i nazwy (Facebook używa kluczy "email" i "name")
        String email = (String) attributes.get("email");
        String fullName = (String) attributes.get("name");

        logger.info("Rozpoczęto logowanie OAuth2 ({}): email={}", registrationId, email);

        if (email == null) {
            logger.error("Facebook nie dostarczył adresu email dla użytkownika.");
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token", "Facebook email not found", null)
            );
        }

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    logger.info("Użytkownik {} już istnieje w bazie.", email);
                    return existingUser;
                })
                .orElseGet(() -> registerNewFacebookUser(registrationId, email, fullName));

        return new DefaultOAuth2User(
                user.getAuthorities(),
                attributes,
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private User registerNewFacebookUser(String registrationId, String email, String fullName) {
        logger.info("Rejestracja nowego użytkownika z Facebooka: {}", email);

        User user = new User();
        user.setEmail(email);

        String baseName = (fullName != null) ? fullName.replace(" ", "_").toLowerCase() : email.split("@")[0];
        user.setUsername(registrationId + "_" + baseName + "_" + System.currentTimeMillis() % 1000);

        user.setPassword(passwordEncoder.encode("OAUTH2_FB_DUMMY_" + registrationId));
        user.setRole(Role.ROLE_USER);
        user.setLocked(false);

        return userRepository.save(user);
    }
}