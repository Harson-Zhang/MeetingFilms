package com.harson.uidprovider.controller;

import com.harson.uidprovider.service.DefaultUidGenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uid")
public class UidController {
    @Autowired
    DefaultUidGenServiceImpl uidGenService;

    @GetMapping("/getUid")
    public String getUid(){
        return String.valueOf(uidGenService.getUid());
    }
}
