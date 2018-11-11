package com.jackiew.demo.rio.vo;

import com.jackiew.demo.rio.excel.annotation.CellInfo;
import lombok.Data;

@Data
public class GlobalRIo {
    @CellInfo(title = "gblPartNo", columnIndex = 0)
    private String gblPartNo;
    @CellInfo(title = "gblModelName", columnIndex = 1)
    private String gblModelName;
    @CellInfo(title = "gblAllocQty", columnIndex = 2)
    private int gblAllocQty;
}
