package com.jackiew.demo.rio.vo;

import com.jackiew.demo.rio.excel.annotation.CellInfo;
import com.jackiew.demo.rio.excel.annotation.ExcelExportClass;
import lombok.Data;

@Data
@ExcelExportClass
public class CompareRIo {
    //global data
    @CellInfo(title = "gblPartNo", columnIndex = 0)
    private String gblPartNo;
    @CellInfo(title = "gblModelName", columnIndex = 1)
    private String gblModelName;
    @CellInfo(title = "gblAllocQty", columnIndex = 2)
    private int gblAllocQty;
    //local data
    @CellInfo(title = "partNo", columnIndex = 3)
    private String partNo;
    @CellInfo(title = "modelName", columnIndex = 4)
    private String modelName;
    @CellInfo(title = "orderType", columnIndex = 5)
    private int orderType;
    @CellInfo(title = "rioQty", columnIndex = 6)
    private int rioQty;
    @CellInfo(title = "allocQty", columnIndex = 7)
    private int allocQty;
    @CellInfo(title = "offset", columnIndex = 8)
    private int offset;
    @CellInfo(title = "availQty", columnIndex = 9)
    private int availQty;
    @CellInfo(title = "diff", columnIndex = 10)
    private int diff;

    public boolean isIssueData() {
        return gblAllocQty > rioQty && availQty > 0 || offset < 0;
    }

    public int getDiff() {
        return gblAllocQty - rioQty;
    }
}
