package com.carsonlius.services.impl;

import com.carsonlius.handler.MergeRunnable;
import com.carsonlius.handler.SplitRunnable;
import com.carsonlius.services.FileHandleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月23日 16时31分
 * @contact
 * @company
 */
@Service
public class FileHandleServiceImpl implements FileHandleService {

    /**
     * 分割成指定bite大小的文件
     * */
    @Override
    public List<String> splitBySize(String fileName, int byteSize) {
        List<String> parts = new ArrayList<String>();
        File file = new File(fileName);

        // 计算文件的数量
        int count = (int) Math.ceil(file.length() / (double) byteSize);
        int countLen = (count + "").length();

        // 线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(count,
                count * 3, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(count * 2));

        for (int i = 0; i < count; i++) {
            String partFileName = file.getAbsolutePath() + file.getName() + "."
                    + StringUtils.leftPad((i + 1) + "", countLen, '0') + ".part";

            threadPool.execute(new SplitRunnable(byteSize, i * byteSize,
                    partFileName, file));
            parts.add(partFileName);
        }
        return parts;
    }

   /**
    * 合并文件
    *
    * @param dirPath 拆分文件所在目录名
    * @param partFileSuffix 拆分文件后缀名
    * @param partFileSize 拆分文件的字节数大小
    * @param mergeFileName 合并后的文件名
    */
    @Override
    public void mergePartFiles(String dirPath, String partFileSuffix, int partFileSize, String mergeFileName) throws IOException {
        // 要操作的文件列表
        File dirFile = new File(dirPath);
        File[] files = dirFile.listFiles((dir, name) -> name.endsWith(partFileSuffix));


        ArrayList<File> partFiles = new ArrayList<>();
        Collections.addAll(partFiles, files);

        // 排序
        Collections.sort(partFiles, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        // 合并后文件名
        RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFileName, "rw");

        // 预设文件大小
        randomAccessFile.setLength(partFileSize * (partFiles.size() - 1)
                + partFiles.get(partFiles.size() - 1).length());

        randomAccessFile.close();

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                partFiles.size(), partFiles.size() * 3, 1, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(partFiles.size() * 2));

        for (int i = 0; i < partFiles.size(); i++) {
            threadPool.execute(new MergeRunnable(i * partFileSize,
                    mergeFileName, partFiles.get(i)));
        }
    }

    /**
     * 解压文件
     * */
    @Override
    public void unZip(String srcPath, String dest) throws IOException {

        File file = new File(srcPath);
        if (!file.exists()) {
            throw new RuntimeException(srcPath + "所指文件不存在");
        }
        ZipFile zf = new ZipFile(file, Charset.forName("gbk"));
        Enumeration entries = zf.entries();
        ZipEntry entry = null;
        while (entries.hasMoreElements()) {
            entry = (ZipEntry) entries.nextElement();
            System.out.println("解压" + entry.getName());
            if (entry.isDirectory()) {
                String dirPath = dest + File.separator + entry.getName();
                Path path = Paths.get(dirPath);
                Files.createDirectories(path);

            } else {
                // 表示文件
                File f = new File(dest + File.separator + entry.getName());
                if (!f.exists()) {
                    String dirs = f.getParent();
                    Path path = Paths.get(dirs);
                    Files.createDirectories(path);
                }
                f.createNewFile();
                // 将压缩文件内容写入到这个文件中
                InputStream is = zf.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(f);
                int count;
                byte[] buf = new byte[8192];
                while ((count = is.read(buf)) != -1) {
                    fos.write(buf, 0, count);
                }
                is.close();
                fos.close();
            }
        }
    }

}
