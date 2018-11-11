package com.jackiew.demo.rio.excel;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface ExcelParser<T> {
    List<T> parse(Sheet sheet);
}
