package com.dashboardTemplate.dashboardTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello1")
    public String hello() {
        return "Hello, DashboardTemplate";
    }

    //Jenkins 테스트
}
