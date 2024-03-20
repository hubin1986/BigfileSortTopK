package org.woven.worker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.woven.tools.RuntimeInfo;

/**
 * @author: hubin
 * @date: 2024/1/20 4:01
 * @description: single instance for multithread work
 */
public class LimitRunningPool {
    private static LimitRunningPool instance=new LimitRunningPool();
    private static ExecutorService executorSubSort;
    private static ExecutorService executorMerge;
    private final Semaphore semaphoreSubSort;
    private final Semaphore semaphoreMerge;
    private final AtomicInteger runningNumber = new AtomicInteger(0);




    private LimitRunningPool(){
        int size = RuntimeInfo.getConcurrentSize();
        executorSubSort = Executors.newFixedThreadPool(size); // fix thread pool
        semaphoreSubSort = new Semaphore(size);

        int mergeSize = size/3>0?size/3:1;
        executorMerge = Executors.newFixedThreadPool(mergeSize); // fix thread pool
        semaphoreMerge = new Semaphore(mergeSize);
    }

    public static LimitRunningPool getInstance(){
        return instance;
    }

    /**
     * submit a worker run in the pool
     */
    public <T> Future<T> submitSubSort(final Callable<T> task) throws InterruptedException {
        semaphoreSubSort.acquire();
        runningNumber.addAndGet(1);
        return executorSubSort.submit(() -> {
            try {
                return task.call();
            } finally {
                runningNumber.addAndGet(-1);
                semaphoreSubSort.release();
            }
        });
    }

    /**
     * submit a worker run in the pool
     */
    public <T> Future<T> submitSubMerge(final Callable<T> task) throws InterruptedException {
        semaphoreMerge.acquire();
        runningNumber.addAndGet(1);
        return executorMerge.submit(() -> {
            try {
                return task.call();
            } finally {
                runningNumber.addAndGet(-1);
                semaphoreMerge.release();
            }
        });
    }


    /**
     * shutdown and wait
     */
    public void shutDownAndWait(){
        executorSubSort.shutdown();
        executorMerge.shutdown();
        try {
            //wait all work done
            executorSubSort.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            executorMerge.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public boolean isAllDone(){
        return runningNumber.get() == 0;
    }


}
