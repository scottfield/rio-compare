package com.jackiew.demo.rio.excel.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CellInfo {
    /**
     * column title
     *
     * @return
     */
    String title();

    /**
     * column index
     *
     * @return
     */
    int columnIndex();
}
