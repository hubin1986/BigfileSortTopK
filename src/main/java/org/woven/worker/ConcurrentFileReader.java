package org.woven.worker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;

/**
 * @author: hubin
 * @date: 2024/1/21 8:16
 * @description:
 */

public class ConcurrentFileReader {
    private int threadNum = 3;
    private String filePath;
    private int bufSize = 8024;//8024


    public ConcurrentFileReader(String filePath, int bufSize, int threadNum) {
        this.threadNum = threadNum;
        this.bufSize = bufSize;
        this.filePath = filePath;
    }

    /**
     * start multithread
     */
    public void startRead() {
        FileChannel infile = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "r");
            infile = raf.getChannel();
            long size = infile.size();
            long subSize = size / threadNum;
            for (int i = 0; i < threadNum; i++) {
                long startIndex = i * subSize;
                if (size % threadNum > 0 && i == threadNum - 1) {
                    subSize += size % threadNum;
                }
                RandomAccessFile accessFile = new RandomAccessFile(filePath, "r");
                FileChannel inch = accessFile.getChannel();
                LimitRunningPool.getInstance().submitSubSort(new MultiThreadReader(inch, startIndex, subSize));
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            try {
                if (infile != null) {
                    infile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * reader in thread
     */
    public class MultiThreadReader implements Callable<Long> {
        private FileChannel channel;
        private long startIndex;
        private long rSize;
        private long lineNumber;
        private DataProcessHandler dataProcessHandler;

        public MultiThreadReader(FileChannel channel, long startIndex, long rSize) {
            this.channel = channel;
            this.startIndex = startIndex > 0 ? startIndex - 1 : startIndex;
            this.rSize = rSize;
            dataProcessHandler = new FileLineDataHandler() ;
        }

        public Long call() {
            readByLine();
            return lineNumber;
        }

        /**
         * read by line
         * @return
         */
        public void readByLine() {
            try {
                ByteBuffer rbuf = ByteBuffer.allocate(bufSize);
                channel.position(startIndex);// set start position
                long endIndex = startIndex + rSize;//set end position
                byte[] temp = new byte[0];// cache last read
                //int LF = "\n".getBytes()[0];//
                int LF = System.lineSeparator().getBytes()[0];
                boolean isEnd = false;//judge end
                boolean isWholeLine = false;
                long lineCount = 0;
                long endLineIndex = startIndex;// handle current position
                while (channel.read(rbuf) != -1 && !isEnd) {
                    int position = rbuf.position();
                    byte[] rbyte = new byte[position];
                    rbuf.flip();
                    rbuf.get(rbyte);
                    int startnum = 0;
                    //judge LF
                    //not a whole line ,read next line
                    for (int i = 0; i < rbyte.length; i++) {
                        endLineIndex++;
                        if (rbyte[i] == LF) {
                            if (channel.position() == startIndex) {
                                isWholeLine = true;
                                startnum = i + 1;
                            } else {
                                byte[] line = new byte[temp.length + i - startnum + 1];
                                System.arraycopy(temp, 0, line, 0, temp.length);
                                System.arraycopy(rbyte, startnum, line, temp.length, i - startnum + 1);
                                startnum = i + 1;
                                lineCount++;
                                temp = new byte[0];
                                //data process
                                if (startIndex != 0) {
                                    if (lineCount == 1) {
                                        if (isWholeLine) {
                                            lineNumber++;
                                            dataProcessHandler.process(line);
                                        }
                                    } else {
                                        lineNumber++;
                                        dataProcessHandler.process(line);
                                    }
                                } else {
                                    lineNumber++;
                                    dataProcessHandler.process(line);
                                }
                                //is end
                                if (endLineIndex >= endIndex) {
                                    isEnd = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!isEnd && startnum < rbyte.length) {//not a line
                        byte[] temp2 = new byte[temp.length + rbyte.length - startnum];
                        System.arraycopy(temp, 0, temp2, 0, temp.length);
                        System.arraycopy(rbyte, startnum, temp2, temp.length, rbyte.length - startnum);
                        temp = temp2;
                    }
                    rbuf.clear();
                }
                //last line without LF
                if (temp.length > 0) {
                    if (dataProcessHandler != null) {
                        lineNumber++;
                        dataProcessHandler.process(temp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    dataProcessHandler.close();
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getThreadNum() {
        return threadNum;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getBufSize() {
        return bufSize;
    }

}