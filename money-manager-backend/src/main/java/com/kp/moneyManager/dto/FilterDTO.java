package com.kp.moneyManager.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilterDTO {

    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private String sortField; // date, amt, name
    private String sortOrder; //asc/dsc
}
