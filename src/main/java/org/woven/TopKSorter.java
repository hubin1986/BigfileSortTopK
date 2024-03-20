package org.woven;

import org.woven.tools.DataHelper;
import org.woven.tools.RuntimeInfo;
import org.woven.worker.ConcurrentFileReader;
import org.woven.worker.Coordinator;
import org.woven.worker.MergeSorter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/19 4:16
 * @description:
 */
public class TopKSorter
{
    /**
     * @param args
     */
    public static void main(String args[]){
        String fileName = null;
        try {
            RuntimeInfo.doInit();
            fileName = RuntimeInfo.getFilePath();
            if(null == fileName||fileName.isEmpty()){
                throw new Exception("file path can not be empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        //concurrent read from file
        ConcurrentFileReader fileReader = new ConcurrentFileReader(fileName,RuntimeInfo.getFileBufferSize(),RuntimeInfo.getReaderConcurrent());
        fileReader.startRead();
        RuntimeInfo.doWaitWorkers();
        //final result
        List<Long> result = new MergeSorter().doSort();
        DataHelper.printIdList(result);
    }



}
