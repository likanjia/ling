package com.ling.upms;

import com.ling.framework.core.result.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {


    @GetMapping("/list")
    public R list() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", 1504186367607832576L);
        map.put("username", "admin");


        return R.ok(map);
    }
}
