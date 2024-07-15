package com.team7.rupiapp.controller;

import com.team7.rupiapp.dto.StandardResponseModel;
import com.team7.rupiapp.util.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @PostMapping("/create")
    public ResponseEntity<StandardResponseModel<Void>> createEntity() {
        return ApiResponseUtil.createSuccessResponse("Created", HttpStatus.CREATED);
    }

    @GetMapping("/get-data")
    public ResponseEntity<StandardResponseModel<Map<String, String>>> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("medicalRecordId", "006152");
        return ApiResponseUtil.createSuccessResponseWithData("Success", data, HttpStatus.OK);
    }

    @PostMapping("/error")
    public ResponseEntity<StandardResponseModel<Void>> handleError() {
        Map<String, String> error1 = new HashMap<>();
        error1.put("fieldName", "Error message");

        Map<String, String> error2 = new HashMap<>();
        error2.put("appointmentRegistrasionStatusFailed", "This patient has already booked an appointment!");

        List<Map<String, String>> errors = List.of(error1, error2);

        return ApiResponseUtil.createErrorResponse("error", errors, HttpStatus.BAD_REQUEST);
    }
}