package com.test;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Inventory {
    private String Id;
    //private Integer version;
    private String item;
    private String status ;
    private String name;
    private List<String> tags;
    private List<Stock> instock=new ArrayList<>();
    private List<CommThread> threads = new ArrayList<>();
    private List<Customer> customers=new ArrayList<>();
}
