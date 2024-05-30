package com.accommodation_management_booking.service;

import com.accommodation_management_booking.entity.User;
import com.accommodation_management_booking.repository.UserRepository;
import com.accommodation_management_booking.security.CustomOAuth2User;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User);

        // Fetch user details from the repository or create a new user if it doesn't exist
        User user = userRepository.findByEmail(customOAuth2User.getEmail());
        if (user == null) {
            // First-time login, create a new user and save to the repository
            user = new User();
            user.setEmail(customOAuth2User.getEmail());
            user.setUsername(customOAuth2User.getName());
            user.setProfileComplete(false); // mark as profile not complete
            userRepository.save(user);
        }

        return customOAuth2User;
    }
}
