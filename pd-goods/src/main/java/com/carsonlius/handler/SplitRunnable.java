package com.carsonlius.handler;

import java.io.*;

/**
 * 分割大文件
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月23日 16时34分
 * @contact
 * @company
 */
public class SplitRunnable implements Runnable {
    // 单文件大小
    int byteSize;
    // 子文件名字
    String partFileName;
    // 原文件名字
    File originFile;

    // 开始位置
    int startPos;

    public SplitRunnable(int byteSize, int startPos, String partFileName,
                         File originFile) {
        this.startPos = startPos;
        this.byteSize = byteSize;
        this.partFileName = partFileName;
        this.originFile = originFile;
    }

    @Override
    public void run() {
        RandomAccessFile rFile;
        OutputStream os = null;

        try {
            rFile = new RandomAccessFile(originFile, "r");
            byte[] bytesContent = new byte[byteSize];

            // 移动指针到子文件开头
            rFile.seek(startPos);

            // 读取子文件(-1代表进入了末尾
            // the total number of bytes read into the buffer,
            // or -1 if there is no more data because the end of the file has been reached.)
            int s = rFile.read(bytesContent, 0, bytesContent.length);

            // 写入子文件
            os = new FileOutputStream(partFileName);
            os.write(bytesContent, 0, s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    System.out.println("关闭文件流出错");
                    e.printStackTrace();
                }
            }
        }
    }
}
