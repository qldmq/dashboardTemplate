package com.dashboardTemplate.dashboardTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, DashboardTemplate";
    }

    //Jenkins CI/CD 테스트용
}
