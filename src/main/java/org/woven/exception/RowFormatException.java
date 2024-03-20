package org.woven.exception;

/**
 * @author: hubin
 * @date: 2024/1/22 10:34
 * @description:
 */
public class RowFormatException extends Exception {
    public RowFormatException(String row) {
        super("row format error:"+row);
    }
}
