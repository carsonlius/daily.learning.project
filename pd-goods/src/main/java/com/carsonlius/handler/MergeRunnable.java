package com.carsonlius.handler;

import java.io.*;

/** 合并文件
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月23日 18时41分
 * @contact
 * @company
 */
public class MergeRunnable implements Runnable {
    long startPos;
    String mergeFileName;
    File partFile;

    public MergeRunnable(long startPos, String mergeFileName, File partFile) {
        this.startPos = startPos;
        this.mergeFileName = mergeFileName;
        this.partFile = partFile;
    }

    @Override
    public void run() {
        FileInputStream fs = null;
        RandomAccessFile rFile = null;
        try {
            rFile = new RandomAccessFile(mergeFileName, "rw");

            // 移动指针到子文件开头
            rFile.seek(startPos);

            fs = new FileInputStream(partFile);

            //该方法的返回类型是int，它返回在解除阻塞期间可以从此 FileInputStream 读取的剩余可用字节数。
            byte[] data = new byte[fs.available()];

            fs.read(data);
            fs.close();

            rFile.write(data);
            rFile.close();

        }  catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("结束....");

        }
    }
}
