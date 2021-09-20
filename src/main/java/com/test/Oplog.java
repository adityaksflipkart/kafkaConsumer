package com.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Oplog<T> {
    private T fullDocument;
    @JsonProperty("ns")
    private DbCollection dbCollection;
    private String operationType;
}
