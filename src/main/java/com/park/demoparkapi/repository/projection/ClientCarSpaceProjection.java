package com.park.demoparkapi.repository.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClientCarSpaceProjection {
    String getLicensePlate();
    String getCarBrand();
    String getCarModel();
    String getCarColor();
    String getClientCpf();
    String getReceipt();
    @JsonFormat(pattern = "YYYY-MM-DD hh:mm:ss")
    LocalDateTime getEntryDate();
    @JsonFormat(pattern = "YYYY-MM-DD hh:mm:ss")
    LocalDateTime getEndDate();
     String getCarSpaceCode();
     BigDecimal getValue();
     BigDecimal getDiscount();
}
