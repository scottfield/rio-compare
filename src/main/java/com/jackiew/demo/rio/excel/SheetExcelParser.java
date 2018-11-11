package com.jackiew.demo.rio.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SheetExcelParser<T> implements ExcelParser<T> {
    private boolean hasTitle = true;
    private List<String> fields;
    private Class<T> supportClazz;

    public SheetExcelParser(Class<T> supportClazz) {
        this.supportClazz = supportClazz;
        Field[] fields = supportClazz.getDeclaredFields();
        Arrays.sort(fields, FieldComparator.getComparator());
        this.fields = Arrays.stream(fields).map(Field::getName).collect(toList());
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
            for (Cell cell : row) {
                int columnIndex = cell.getColumnIndex();
                Object cellValue = getCellValue(cell);
                String field = fields.get(columnIndex);
                wrapper.setPropertyValue(field, cellValue);
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
