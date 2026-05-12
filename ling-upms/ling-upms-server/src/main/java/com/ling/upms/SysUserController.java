package com.ling.upms;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {


    @GetMapping("/list")
    public ResponseEntity<String> list() {

        return ResponseEntity.ok("success");
    }
}
