package com.jackiew.demo.rio.excel;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.jackiew.demo.rio.excel.annotation.CellInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class SheetExcelParser<T> implements ExcelParser<T> {
    private boolean hasTitle = true;
    private Map<String, Integer> fields;
    private Class<T> supportClazz;

    public SheetExcelParser(Class<T> supportClazz) {
        this.supportClazz = supportClazz;
        Field[] fields = supportClazz.getDeclaredFields();
        this.fields = Arrays.stream(fields).collect(toMap(Field::getName, field -> {
            CellInfo cellInfo = field.getDeclaredAnnotation(CellInfo.class);
            Objects.requireNonNull(cellInfo, "cellInfo cannot be null for field" + field.getName());
            return cellInfo.columnIndex();
        }));
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    public Class<T> getSupportClazz() {
        return supportClazz;
    }

    @Override
    public List<T> parse(Sheet sheet) {
        List<T> rioList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.iterator();
        if (hasTitle) {
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
        }

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Object target = BeanUtils.instantiateClass(supportClazz);
            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);

            for (Map.Entry<String, Integer> field : fields.entrySet()) {
                int columnIndex = field.getValue();
                Cell cell = row.getCell(columnIndex);
                Objects.requireNonNull(cell, "please check cell info configuration,cannot find cell according index:" + columnIndex);
                Object cellValue = getCellValue(cell);
                String fieldName = field.getKey();
                wrapper.setPropertyValue(fieldName, cellValue);
            }
            rioList.add((T) wrapper.getWrappedInstance());
        }
        return rioList;
    }

    private Object getCellValue(Cell cell) {
        if (CellType.STRING.equals(cell.getCellType())) {
            return cell.getStringCellValue();
        }
        if (CellType.NUMERIC.equals(cell.getCellType())) {
            Double value = cell.getNumericCellValue();
            return value.intValue();
        }
        throw new IllegalArgumentException("not supported cell type");
    }
}
