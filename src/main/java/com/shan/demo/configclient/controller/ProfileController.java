package com.shan.demo.configclient.controller;


import com.shan.demo.configclient.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class ProfileController {

    @Autowired
    ProfileService profileService;


    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<Map<String, Object>> getProfileDetails(@PathVariable String profileId) throws ExecutionException, InterruptedException {

        Map<String, Object> response = profileService.getProfileDetails(profileId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}
