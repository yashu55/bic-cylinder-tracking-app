package com.bic.cylinder_tracking_api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {


    @GetMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllUsers() {
        return "OK";
    }

}

