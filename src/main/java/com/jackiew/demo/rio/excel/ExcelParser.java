package com.jackiew.demo.rio.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

public interface ExcelParser<T> {
    List<T> parse(Sheet sheet);
}
