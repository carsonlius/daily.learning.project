package com.carsonlius.services;

import java.io.IOException;
import java.util.List;

/**
 * 文件操作
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月23日 16时29分
 * @contact
 * @company
 */
public interface FileHandleService {
    /**
     *
     * 分割大文件
     * */
    List<String> splitBySize(String fileName, int splitBySize);


    /**
     * 合并文件
     * */
    void mergePartFiles(String dirPath, String partFileSuffix, int partFileSize, String mergeFileName) throws IOException;


    void unZip(String srcPath, String dest) throws IOException;

}
