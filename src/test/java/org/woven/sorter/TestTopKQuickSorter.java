package org.woven.sorter;
import org.junit.*;
import org.woven.tools.DataPair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/21 7:38
 * @description:
 */
public class TestTopKQuickSorter {

    @Test
    public  void testQuickSort(){
        System.out.println("------------------------------------------");
        int k = 4;
        List<DataPair> list = new ArrayList<>();
        list.add(new DataPair(1l,11l));
        list.add(new DataPair(2l,24l));
        list.add(new DataPair(3l,12l));
        list.add(new DataPair(4l,20l));
        list.add(new DataPair(5l,23l));
        list.add(new DataPair(6l,10l));
        list.add(new DataPair(7l,32l));
        list.add(new DataPair(8l,9l));
        list.add(new DataPair(9l,6l));
        list.add(new DataPair(10l,42l));

        TopKQuickSorter sorter = new TopKQuickSorter();
        List result = sorter.findTopK(list,k);
        printList(result);
    }

    private void printList(List<DataPair> list){
        for(DataPair data:list){
            System.out.println(data.getId()+":"+data.getValue());
        }
    }


}
