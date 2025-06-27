package com.dashboardTemplate.dashboardTemplate;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Operation(summary = "/경로 처리", description = "기본 경로 처리")
    @GetMapping("/")
    public String home() {
        return "서버가 실행되었습니다.";
    }
}
