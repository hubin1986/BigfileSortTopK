package org.woven.worker;

import org.woven.tools.DataHelper;
import org.woven.tools.DataPair;
import org.woven.tools.RuntimeInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: hubin
 * @date: 2024/1/22 8:27
 * @description:
 */
public class FileLineDataHandler implements DataProcessHandler{
    private String encode = "UTF-8";
    private List<DataPair> list = new ArrayList<>();
    private DataHelper helper = new DataHelper();
    private int count = 0;
    private int totalCount = 0;

    @Override
    public void close(){
        if(!list.isEmpty()){
            //submit worker
            try {
                Coordinator.getInstance().submitList(list);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void process(byte[] data) {
        try {
            String row = new String(data,encode);
            DataPair pair = helper.parseFromLine(row);
            list.add(pair);
            count++;
            totalCount++;
            if (count == RuntimeInfo.getLinePreThread()){
                List<DataPair> copyList = new ArrayList<>(list);
                //submit worker
                Coordinator.getInstance().submitList(copyList);
                list.clear();
                count = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
