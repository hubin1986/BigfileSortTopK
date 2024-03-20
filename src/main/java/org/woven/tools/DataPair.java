package org.woven.tools;

/**
 * @author: hubin
 * @date: 2024/1/21 7:21
 * @description:
 */
public class DataPair {
    private long id;
    private long value;

    public DataPair(long id, long value){
        this.id = id;
        this.value = value;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setValue(long value){
       this.value = value;
    }

    public long getValue(){
        return value;
    }
}
