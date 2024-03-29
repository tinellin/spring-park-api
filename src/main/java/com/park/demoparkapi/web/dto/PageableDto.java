package com.park.demoparkapi.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PageableDto {
    private List content = new ArrayList<>();

    private boolean first;
    private boolean last;

    @JsonProperty("page")
    private int number;

    private int size;

    @JsonProperty("pageElements")
    private int numberOfElements;

    private int totalPages;

    private int totalElements;
}
