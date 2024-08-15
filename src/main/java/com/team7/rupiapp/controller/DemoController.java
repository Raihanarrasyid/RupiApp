package com.team7.rupiapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.dto.demo.DemoQrisCPMDto;
import com.team7.rupiapp.service.DemoService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/demo")
public class DemoController {
    private final DemoService demoService;
    
    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }
    
    @PostMapping("/qris/cpm")
    public ResponseEntity<Object> demoQrisCPM(@Valid @RequestBody DemoQrisCPMDto demoQrisCPMDto) {
        demoService.demoQrisCPM(demoQrisCPMDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Demo Qris CPM success");
    }
}
