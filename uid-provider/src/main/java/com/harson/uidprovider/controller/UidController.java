package com.harson.uidprovider.controller;

import com.harson.uidprovider.service.UidGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uid")
public class UidController {
    @Autowired
    UidGenService uidGenService;

    @GetMapping("/getUid")
    public String getUid(){
        return String.valueOf(uidGenService.getUid());
    }
}
