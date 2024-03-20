package org.woven.worker;

import org.woven.tools.DataPair;
import org.woven.tools.RuntimeInfo;
import org.woven.sorter.TopKQuickSorter;

import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/21 7:09
 * @description: final stage,merge split,and sort
 */
public class MergeSorter {
    public List<Long> doSort(){
        TopKQuickSorter sorter = new TopKQuickSorter();
        List<DataPair> list = Coordinator.getInstance().getIntermediateList();
        int length = list.size();
        int topK = RuntimeInfo.topK;
        if (length < topK){
            topK = length;
        }
        return sorter.findTopKIds(list,topK);
    }

    public List<DataPair> doSortPair(){
        TopKQuickSorter sorter = new TopKQuickSorter();
        List<DataPair> list = Coordinator.getInstance().getIntermediateList();
        int length = list.size();
        int topK = RuntimeInfo.topK;
        if (length < topK){
            topK = length;
        }
        return sorter.findTopK(list,topK);
    }
}
