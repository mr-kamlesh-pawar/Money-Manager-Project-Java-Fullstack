package com.kp.moneyManager.service;

import com.kp.moneyManager.dto.AuthDTO;
import com.kp.moneyManager.dto.ProfileDTO;
import com.kp.moneyManager.entity.ProfileEntity;
import com.kp.moneyManager.repository.ProfileRepository;
import com.kp.moneyManager.util.EmailAlreadyExistsException;
import com.kp.moneyManager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        if (profileRepository.findByEmail(profileDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + profileDTO.getEmail() + " is already registered");
        }

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile.setPassword(passwordEncoder.encode(newProfile.getPassword()));
        newProfile = profileRepository.save(newProfile);

        //send Activation Mail
        String activationLink = activationURL + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Active Your Money Manager Account.";
        String body = "Click on the following Link to activate your account :" + activationLink;

        emailService.sendMail(newProfile.getEmail(), subject, body);

        return toDTO(newProfile);

    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();

    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        return ProfileDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


    public Boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);

    }


    public boolean checkUserAlreadyActive(String email) {
        Optional<ProfileEntity> profile = profileRepository.findByEmail(email);
        return profile.map(ProfileEntity::getIsActive).orElse(false);
    }


    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile Not found with Email " + authentication.getName()));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentProfile = null;

        if (email == null) {
            currentProfile = getCurrentProfile();
        } else {
            currentProfile = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile Not Found with this email" + email));

        }

        return ProfileDTO.builder()
                .id(currentProfile.getId())
                .fullName(currentProfile.getFullName())
                .email(currentProfile.getEmail())
                .profileImageUrl(currentProfile.getProfileImageUrl())
                .createdAt(currentProfile.getCreatedAt())
                .updatedAt(currentProfile.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getEmail(),
                            authDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(authDTO.getEmail());
            ProfileDTO userProfile = getPublicProfile(authDTO.getEmail());

            return Map.of(
                    "token", token,
                    "user", userProfile,
                    "expiresIn", jwtUtil.getExpirationTime() // Add this to your JwtUtil
            );
        } catch (BadCredentialsException e) {
            // Log the error for debugging
            //logger.error("Authentication failed for user: " + authDTO.getEmail(), e);
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
