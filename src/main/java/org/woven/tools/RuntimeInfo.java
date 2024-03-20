package org.woven.tools;

import org.woven.exception.MemoryNotEnoughException;
import org.woven.exception.TopKException;
import org.woven.worker.LimitRunningPool;

import java.util.Scanner;

/**
 * @author: hubin
 * @date: 2024/1/19 7:45
 * @description:
 */
public class RuntimeInfo {

    private static final int KB = 1024;

    private static final int MB = 1024 * KB;

    private static final int RESERVE_PROCESS = 1;

    private static final int RESERVE_MEMORY = 50;  //MB

    private static final int LINE_PRE_THREAD = 1*MB;

    public static int topK = 0;

    private static final int MEMORY_LIMIT = 100; //MB

    private static int linesPerThread = 0;

    private static int threadPoolSize = 1;

    private static String filePath = "";

    private static int fileBufferSize = 8124;

    public static int getFileBufferSize() {
        return fileBufferSize;
    }


    public static void doInit() throws Exception{
        readFromScanner();
        initLineNumberPerThread();
        initConcurrentSize();
    }

    public static void doInitForTest() throws Exception{
        readFromTest();
        initLineNumberPerThread();
        initConcurrentSize();
    }


    public static void doWaitWorkers(){
        try {
            while (!LimitRunningPool.getInstance().isAllDone()){
                Thread.sleep(20);
            }
            LimitRunningPool.getInstance().shutDownAndWait();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * split run in thread, to account line number for a thead
     * long :8bytes
     * space complexity for sorter: averrage=logn, worest=n
     * list space:n
     * to divide 2
     */
    public static int initLineNumberPerThread() throws Exception {
        long memory = getMemorySize();
        if(memory < MEMORY_LIMIT){
            throw new MemoryNotEnoughException();
        }

        long sortMemory = getMemorySize() - RESERVE_MEMORY;
        int threadPoolSize = getConcurrentSize();
        int sortSpaceReserveFactor = 2;
        int twoNumberInOneLine = 2;
        int lineNumber = (int) sortMemory*MB/threadPoolSize/Long.BYTES/twoNumberInOneLine/sortSpaceReserveFactor;
        if(topK > lineNumber){
            throw new TopKException();
        }

        linesPerThread = lineNumber<LINE_PRE_THREAD?lineNumber:LINE_PRE_THREAD;
        return linesPerThread;
    }

    public static int getLinePreThread(){
        return linesPerThread;
    }

    /**
     * get logic cpu numbers
     */
    public static int getProcessorNumber(){
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * get threadpool size = processors - RESERVE_PROCESS
     */
    public static int initConcurrentSize(){
        int tempNumber = getProcessorNumber()-RESERVE_PROCESS;
        threadPoolSize = tempNumber>2?tempNumber:2;
        return threadPoolSize;
    }

    /**
     * get threadpool size = processors - RESERVE_PROCESS
     */
    public static int getConcurrentSize(){
        return threadPoolSize;
    }

    public static int getReaderConcurrent(){
        return threadPoolSize/2;
    }

    /**
     * get logic cpu numbers by MB
     */
    public static long getMemorySize(){
        return Runtime.getRuntime().totalMemory()/MB;
    }

    /**
     * read from scanner
     */
    public static void readFromScanner(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("please input abs file path=:");
        String str = scanner.next();
        filePath = str;
        System.out.println("please input a number, X=:");
        topK = scanner.nextInt();
        scanner.close();
    }

    /**
     * read from scanner
     */
    public static void readFromTest(){
        filePath = "/Users/hubin/dev/test1";
        topK = 8;

    }

    public static String getFilePath(){
        return filePath;
    }

}
