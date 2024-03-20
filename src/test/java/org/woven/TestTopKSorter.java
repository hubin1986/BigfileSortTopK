package org.woven;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.woven.tools.DataHelper;
import org.woven.tools.DataPair;
import org.woven.tools.RuntimeInfo;
import org.woven.worker.ConcurrentFileReader;
import org.woven.worker.Coordinator;
import org.woven.worker.FileLineDataHandler;
import org.woven.worker.MergeSorter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

/**
 * @author: hubin
 * @date: 2024/1/21 8:03
 * @description:
 */
public class TestTopKSorter {
    @Test
    public void TestGenerateTestData(){
        String filePath = "/Users/hubin/dev/test1";
        long lines = 10000*10000;
        long min = 10000*10000;
        long max = 10000*100000;
        long minValue = 10000*10000;
        long maxValue = 10000*100000;
        Random random = new Random();
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i=0;i<lines;i++){
                long idValue = min + (long) (Math.random() * (max - min + 1));
                long value = minValue + (long) (Math.random() * (maxValue - minValue + 1));
                String content = ""+idValue+" " + value + System.getProperty("line.separator");
                //System.out.println("content:"+content);
                bufferedWriter.write(content);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            System.out.println("File created and content written successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load data from file
     */
    @Test
    public void TestLoadAllSortInSingleThread() throws Exception {
        StopWatch stopWatch = StopWatch.createStarted();
        RuntimeInfo.doInitForTest();
        String fileName = RuntimeInfo.getFilePath();
        if(null == fileName||fileName.isEmpty()){
            throw new Exception("file path can not be null");
        }

        int bufferSize = 81920;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)),bufferSize);

        int count = 0;
        String row = "";
        List<DataPair> list = new ArrayList<>();
        DataHelper helper = new DataHelper();
        while ( (row = br.readLine()) != null){
            try {
                DataPair data = helper.parseFromLine(row);
                list.add(data);
                count++;
            }catch (Exception e){
                //skip this data
                e.printStackTrace();
                continue;
            }
        }
        System.out.println("read time:"+stopWatch.getTime());

        Future<List<DataPair>> sortResult = null;
        if(!list.isEmpty()){
            //submit worker
            System.out.println("we have row number:"+ count);
            //DataHelper.printList(list);
            sortResult = Coordinator.getInstance().submitList(list);
            //list.clear();
        }
        br.close();
        List<DataPair> result = sortResult.get();
        System.out.println("sort time:"+stopWatch.getTime());
        System.out.println("------------------------------------");
        DataHelper.printList(result);
    }

    /**
     * load data from multithread
     */
    @Test
    public void TestLoadAllSortInMultiThread() throws Exception {
        RuntimeInfo.doInitForTest();
        String fileName = RuntimeInfo.getFilePath();
        if(null == fileName||fileName.isEmpty()){
            throw new Exception("file path can not be null");
        }
        ConcurrentFileReader fileReader = new ConcurrentFileReader(fileName,RuntimeInfo.getFileBufferSize(),RuntimeInfo.getReaderConcurrent());
        fileReader.startRead();
        RuntimeInfo.doWaitWorkers();
        //final result
        List<DataPair> result = new MergeSorter().doSortPair();
        DataHelper.printList(result);
    }

}
