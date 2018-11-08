package com.jackiew.demo.rio.vo;

import lombok.Data;

@Data
public class LocalRIo {
    private String partNo;
    private String modelName;
    private int orderType;
    private int rioQty;
    private int allocQty;
    private int offset;
    private int availQty;
}
