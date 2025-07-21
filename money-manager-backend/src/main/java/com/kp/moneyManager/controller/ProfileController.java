package com.kp.moneyManager.controller;

import com.kp.moneyManager.dto.ProfileDTO;
import com.kp.moneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
     public   ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO){
        ProfileDTO registeredProfile =  profileService.registerProfile(profileDTO);
         return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);

      }

      @GetMapping("/activate")
      public ResponseEntity<String>  activateProfile(@RequestParam String token){

        //check user already active or not
          boolean checkUserAlreadyActive = profileService.checkUserAlreadyActive(token);
          if(checkUserAlreadyActive){
              return ResponseEntity.status(200).body("Your Profile is Already Activated, Please Log in");

          }

        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.status(200).body("Your Profile is Activated");

        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token is Not Found or Already Used");
        }
    }
}
