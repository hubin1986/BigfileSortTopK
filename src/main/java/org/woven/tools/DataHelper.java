package org.woven.tools;

import org.apache.commons.lang3.StringUtils;
import org.woven.exception.RowFormatException;

import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/21 10:30
 * @description:parse row from file
 */
public class DataHelper {

    /**
     * @param row
     * row eg : 18721008 739
     */
    public DataPair parseFromLine(String row) throws Exception{
        if(row == null||row.isEmpty()){
            throw new RowFormatException("empty");
        }

        String[] splits = row.trim().split("\\s+");
        //splits length should be 2
        if(splits.length != 2){
            throw new RowFormatException("length error:"+splits.length);
        }

        if(!StringUtils.isNumeric(splits[0])){
            throw new RowFormatException("id is not number:"+splits[0]);
        }

        if(!StringUtils.isNumeric(splits[1])){
            throw new RowFormatException("value is not number:"+splits[1]);
        }

        return new DataPair(Long.valueOf(splits[0]),Long.valueOf(splits[1]));
    }

    public static void printList(List<DataPair> list){
        for(DataPair data:list){
            System.out.println(data.getId()+":"+data.getValue());
        }
    }

    public static void printIdList(List<Long> list){
        for(Long data:list){
            System.out.println(data);
        }
    }
}
