package com.jackiew.demo.rio.excel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
