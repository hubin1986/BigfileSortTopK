package org.woven.worker;

import org.woven.tools.DataPair;
import org.woven.tools.RuntimeInfo;
import org.woven.sorter.TopKQuickSorter;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author: hubin
 * @date: 2024/1/20 4:35
 * @description: sort part of a file
 */
public class SorterWorker implements Callable<List<DataPair>> {

    private List<DataPair> sortNums;
    private List<DataPair> resultList;

    public SorterWorker(List<DataPair> list){
        this.sortNums = list;
    }

    protected void doSort(){
        TopKQuickSorter sorter = new TopKQuickSorter();
        int length = sortNums.size();
        int topK = RuntimeInfo.topK;
        if (length<topK){
            topK = length;
        }
        resultList = sorter.findTopK(sortNums,topK);
    }


    @Override
    public List<DataPair> call() throws Exception {
        doSort();
        //avoid dead lock,submit in a new pool
        Coordinator.getInstance().addSubResults(resultList);
        return resultList;
    }
}
