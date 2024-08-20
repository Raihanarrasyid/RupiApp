package com.team7.rupiapp.dto.account;

import lombok.Data;

@Data
public class PageableDto {
    private int pageNumber;
    private int pageSize;
    private boolean last;
    private boolean first;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private boolean empty;
}

