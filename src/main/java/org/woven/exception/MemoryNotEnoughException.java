package org.woven.exception;

/**
 * @author: hubin
 * @date: 2024/1/21 3:38
 * @description:
 */
public class MemoryNotEnoughException extends Exception {

    public MemoryNotEnoughException() {
        super("memory not enough, 100M at least");
    }
}
