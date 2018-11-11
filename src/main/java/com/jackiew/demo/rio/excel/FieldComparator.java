package com.jackiew.demo.rio.excel;

import com.jackiew.demo.rio.excel.annotation.CellInfo;

import java.lang.reflect.Field;
import java.util.Comparator;

public class FieldComparator implements Comparator<Field> {
    private static final FieldComparator comparator = new FieldComparator();

    @Override
    public int compare(Field o1, Field o2) {
        CellInfo cell1 = o1.getDeclaredAnnotation(CellInfo.class);
        CellInfo cell2 = o2.getDeclaredAnnotation(CellInfo.class);
        return cell1.columnIndex() - cell2.columnIndex();
    }

    public static FieldComparator getComparator() {
        return comparator;
    }
}
