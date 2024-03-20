package org.woven.worker;

/**
 * @author: hubin
 * @date: 2024/1/21 8:26
 * @description:
 */
public interface DataProcessHandler
{
    void process(byte[] data);

    void close();
}
