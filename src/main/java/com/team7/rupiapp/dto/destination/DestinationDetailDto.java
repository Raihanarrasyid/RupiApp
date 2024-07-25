package com.team7.rupiapp.dto.destination;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DestinationDetailDto {
    private String fullname;

    @Size(min = 10, max = 10, message = "account_number must be 10 characters")
    private String accountNumber;
    private String bankName="Rupi App";
}
