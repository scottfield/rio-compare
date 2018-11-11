package com.jackiew.demo.rio.vo;

import com.jackiew.demo.rio.excel.annotation.CellInfo;
import lombok.Data;

@Data
public class LocalRIo {
    @CellInfo(title = "partNo", columnIndex = 0)
    private String partNo;
    @CellInfo(title = "modelName", columnIndex = 1)
    private String modelName;
    @CellInfo(title = "orderType", columnIndex = 2)
    private int orderType;
    @CellInfo(title = "rioQty", columnIndex = 3)
    private int rioQty;
    @CellInfo(title = "allocQty", columnIndex = 4)
    private int allocQty;
    @CellInfo(title = "offset", columnIndex = 5)
    private int offset;
    @CellInfo(title = "availQty", columnIndex = 6)
    private int availQty;
}
