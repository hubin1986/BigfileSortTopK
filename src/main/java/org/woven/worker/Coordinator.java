package org.woven.worker;

import org.woven.tools.DataPair;
import org.woven.tools.RuntimeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hubin
 * @date: 2024/1/21 8:24
 * @description:  merge result of SubSortWorker ， and submit a new worker
 */
public class Coordinator {

    private List<DataPair> intermediateList = new ArrayList<>();

    private static Coordinator instance=new Coordinator();

    private Coordinator(){ }

    public static Coordinator getInstance(){
        return instance;
    }

    public synchronized void addSubResults(List<DataPair> list){
        intermediateList.addAll(list);
        //too long to summit a new SorterWorker
        if(intermediateList.size()> RuntimeInfo.getLinePreThread()){
            try {
                LimitRunningPool.getInstance().submitSubMerge(new SorterWorker(list));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<DataPair> getIntermediateList(){
        return intermediateList;
    }

    public Future<List<DataPair>> submitList(List<DataPair> list) throws InterruptedException {
       return LimitRunningPool.getInstance().submitSubSort(new SorterWorker(list));
    }

}
