package com.team7.rupiapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void createEntity_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/demo/create"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"success\":true,\"message\":\"Created\"}"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void getData_ShouldReturnData() throws Exception {
        mockMvc.perform(get("/demo/get-data"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"success\":true,\"message\":\"Success\",\"data\":{\"medicalRecordId\":\"006152\"}}"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void handleError_ShouldReturnErrors() throws Exception {
        mockMvc.perform(post("/demo/error"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"success\":false,\"message\":\"error\",\"errors\":[{\"fieldName\":\"Error message\"},{\"appointmentRegistrasionStatusFailed\":\"This patient has already booked an appointment!\"}]}"))
                .andDo(print());
    }
}