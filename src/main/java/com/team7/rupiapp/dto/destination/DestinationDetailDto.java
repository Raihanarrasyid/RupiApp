package com.team7.rupiapp.dto.destination;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class DestinationDetailDto {
    private String fullname;

    @Column(length = 10)
    private String accountNumber;
    private String bankName="Rupi App";
}
