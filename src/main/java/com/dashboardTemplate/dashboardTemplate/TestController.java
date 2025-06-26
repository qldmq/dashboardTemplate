package com.dashboardTemplate.dashboardTemplate;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Operation(summary = "jenkins 자동배포 테스트2", description = "자동배포 테스트2")
    @GetMapping("/hello1")
    public String hello() {
        return "Hello, DashboardTemplate";
    }

    //Jenkins 자동 배포 
}
