package com.books.library.web.rest;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {
 String name();
 String numberFormat() default "General";
} 