package com.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentKey {
    @JsonProperty("_id")
    private String id;
}
