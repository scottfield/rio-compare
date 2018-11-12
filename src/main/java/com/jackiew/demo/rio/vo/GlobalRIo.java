package com.jackiew.demo.rio.vo;

import com.jackiew.demo.rio.excel.annotation.CellInfo;
import lombok.Data;

@Data
public class GlobalRIo {
    @CellInfo(title = "gblPartNo", columnIndex = 1)
    private String gblPartNo;
    @CellInfo(title = "gblModelName", columnIndex = 2)
    private String gblModelName;
    @CellInfo(title = "gblAllocQty", columnIndex = 5)
    private int gblAllocQty;
}
