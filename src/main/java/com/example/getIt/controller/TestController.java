package com.example.getIt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "test")
public class TestController {
    @GetMapping("/log")
    public String getUserInfo(){
        return "Success Test";
    }
}
