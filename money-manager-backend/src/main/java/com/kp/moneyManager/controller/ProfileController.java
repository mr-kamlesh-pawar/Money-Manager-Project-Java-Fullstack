package com.kp.moneyManager.controller;

import com.kp.moneyManager.dto.AuthDTO;
import com.kp.moneyManager.dto.ProfileDTO;
import com.kp.moneyManager.entity.ProfileEntity;
import com.kp.moneyManager.repository.ProfileRepository;
import com.kp.moneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @PostMapping("/register")
     public   ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile =  profileService.registerProfile(profileDTO);
         return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);

      }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token) {
        Optional<ProfileEntity> profileOpt = profileRepository.findByActivationToken(token);

        if (profileOpt.isPresent()) {
            ProfileEntity profile = profileOpt.get();

            if (profile.getIsActive()) {
                return ResponseEntity.ok("Your Profile is Already Activated, Please Log in");
            }

            boolean isActivated = profileService.activateProfile(token);
            if (isActivated) {
                return ResponseEntity.status(200).body("Your Profile is Activated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token is Not Found or Already Used");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Activation Token");
        }
    }



    //login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            // First check if user exists
            Optional<ProfileEntity> profileOpt = profileRepository.findByEmail(authDTO.getEmail());
            if (profileOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("Message", "User not found"));
            }

            // Then check if active
            if (!profileOpt.get().getIsActive()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("Message", "User Profile is Not Active, Please Active Your Account First"));
            }

            // Then attempt authentication
            Map<String, Object> response = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Message", "Invalid email or password"));
        }
    }


    @GetMapping("/test")
    public String test(){

        return "Running Sucessfully";
    }

}
