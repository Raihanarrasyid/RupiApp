package com.team7.rupiapp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StandardResponseModel<T> {
    private Boolean success; // true, false
    private String message; // "user created successfully"
    private T data;
    private List<Map<String, String>> errors; // errors message from exception
}