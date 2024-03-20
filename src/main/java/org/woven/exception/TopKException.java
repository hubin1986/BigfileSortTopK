package org.woven.exception;

/**
 * @author: hubin
 * @date: 2024/1/21 4:01
 * @description:
 */
public class TopKException extends Exception {
    public TopKException() {
        super("top k to big to sort,please enlarge the memory");
    }
}
