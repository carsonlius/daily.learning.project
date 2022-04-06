package com.carsonlius.controller;

import com.alibaba.fastjson.JSONObject;
import com.carsonlius.base.R;
import com.carsonlius.dto.WriteFileDto;
import com.carsonlius.services.FileHandleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.Runtime.getRuntime;


/**
 * 文件操作
 *
 * @version V1.0
 * @author: liusen
 * @date: 2022年03月14日 18时46分
 * @contact
 * @company
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件操作模块")
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    // 文件及文件夹操作，创建，读取，修改，删除，权限修改、大文件的分割、压缩、文件读取按行转换成map，数组
    /**
     * 要操作的文件
     */
    private static final String FILE_NAME_LOCAL = "project.log";
    /**
     * 绝对路径目录
     */
    private static final String ABSTRACT_PATH = "/Users/carsonlius/project/2022/03/dynamic-datasource-project/pd-goods/src/main/resources";

    private File file;

    @Autowired
    private FileHandleService fileHandleService;

    /**
     * 目录深度
     */
    int depth = 0;


    @ApiOperation(value = "下载支付宝账单", httpMethod = "GET", response = R.class)
    @GetMapping("/downloadFile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "文件地址", required = true, defaultValue = "", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "fileName", value = "保存后的文件名", required = true, defaultValue = "alipay.zip", paramType = "query", dataType = "String")
    })
    public R<String> downloadFile(@RequestParam(name = "url", required = true) String url, @RequestParam(name = "fileName", defaultValue = "alipay.zip") String fileName) throws IOException {

        //
        URL urlResource = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlResource.openConnection();
        // 设置超时间为3秒
        conn.setConnectTimeout(30 * 1000);
        // 防止屏蔽程序抓取而返回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 得到输入流
        InputStream inputStream = conn.getInputStream();
        // 获取字节数组
        byte[] getData = readInputStream(inputStream);

        // 文件保存位置
        File saveDir = new File(ABSTRACT_PATH);
        if (!saveDir.exists()) {
            Path path = Paths.get(ABSTRACT_PATH);
            Files.createDirectories(path);
        }
        String fileFullPath = ABSTRACT_PATH + File.separator + fileName;
        File file = new File(fileFullPath);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }

        logger.info("info:" + url + " download success");

        // 解压文件
        fileHandleService.unZip(fileFullPath, ABSTRACT_PATH+ File.separator + "unzip");


        String response = saveDir + File.separator + fileName;
        return R.success(response);
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * 记录文件名
     */
    private List<String> listFiles = new ArrayList<>();

    @ApiOperation(value = "合并大文件", httpMethod = "GET", response = R.class)
    @GetMapping("/mergeFile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "partFileSuffix", value = "文件后缀", required = true, defaultValue = ".part", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "mergeFileName", value = "合并后的文件名", required = true, defaultValue = "project2.log", paramType = "query", dataType = "String")
    })
    public R<String> mergeFile(@RequestParam(name = "partFileSuffix", defaultValue = ".part") String partFileSuffix, @RequestParam(name = "mergeFileName", defaultValue = "project2.log") String mergeFileName) throws IOException {
        // 100MB
        int splitBySize = 100 * 1024 * 1024;
        mergeFileName = StringUtils.join(ABSTRACT_PATH, "/", mergeFileName);
        fileHandleService.mergePartFiles(ABSTRACT_PATH, partFileSuffix, splitBySize, mergeFileName);

        return R.success("成功合并");
    }

    @ApiOperation(value = "拆分大文件", httpMethod = "GET", response = R.class)
    @GetMapping("/splitBySize")
    @ApiImplicitParam(name = "fileName", value = "文件名字", required = true, defaultValue = "project.log", paramType = "query", dataType = "String")
    public R<List<String>> splitBySize(@RequestParam(name = "fileName", defaultValue = "project.log") String fileName) {
        fileName = StringUtils.join(ABSTRACT_PATH, "/", fileName);

        // 100MB
        int splitBySize = 100 * 1024 * 1024;
        List<String> response = fileHandleService.splitBySize(fileName, splitBySize);

        return R.success(response);
    }


    @ApiOperation(value = "压缩文件(ZipOutputStream)", httpMethod = "GET", response = R.class)
    @GetMapping("/压缩文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "待压缩文件名", required = false, defaultValue = "project.log", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "zipOutName", value = "压缩后文件名", required = false, defaultValue = "project.zip", paramType = "query", dataType = "String"),

    })
    public R<String> zipFile(@RequestParam(name = "fileName", defaultValue = "project.log") String fileName, @RequestParam(name = "zipOutName", defaultValue = "project.zip", required = true) String zipOutName) throws IOException {
        List<String> fileNames = new ArrayList<>();
        fileName = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        zipOutName = StringUtils.join(ABSTRACT_PATH, "/", zipOutName);
        fileNames.add(fileName);

        ZipOutputStream zipOutputStream = null;
        WritableByteChannel writableByteChannel = null;
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipOutName));
            writableByteChannel = Channels.newChannel(zipOutputStream);
            for (String sourceFile : fileNames) {
                File source = new File(sourceFile);
                zipOutputStream.putNextEntry(new ZipEntry(source.getName()));
                FileChannel fileChannel = new FileInputStream(sourceFile).getChannel();

                while (fileChannel.read(buffer) != -1) {
                    //更新缓存区位置
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        writableByteChannel.write(buffer);
                    }
                    buffer.rewind();
                }
                fileChannel.close();
            }

        } catch (Exception e) {
            logger.error("batchZipFiles error  fileNames:" + JSONObject.toJSONString(fileNames), e);
        } finally {
            zipOutputStream.close();
            writableByteChannel.close();
            buffer.clear();
        }
        return R.success("压缩成功");
    }

    @ApiOperation(value = "删除非空目录", httpMethod = "GET", response = R.class)
    @GetMapping("/delNotEmptyDir")
    @ApiImplicitParam(name = "dirName", value = "文件目录", required = false, defaultValue = "logs", paramType = "query", dataType = "String")
    public R<String> delNotEmptyDir(@RequestParam(name = "dirName", defaultValue = "logs") String dirName) {
        String fullDirPath = StringUtils.join(ABSTRACT_PATH, "/", dirName);
        File file = new File(fullDirPath);

        removeDir(file);
        return R.success("删除成功");
    }
    /**
     * 删除非空目录
     */
    public static boolean removeDir(File file) {
        File[] fileArr = file.listFiles();
        if (fileArr != null) {
            for (File files : fileArr) {
                if (files.isDirectory()) {
                    removeDir(files);
                } else {
                    files.delete();
                }
            }
        }
        return file.delete();
    }

    @ApiOperation(value = "解压文件(目录)", httpMethod = "GET", response = R.class)
    @GetMapping("/unzip")
    @ApiImplicitParam(name = "filename", value = "文件名", required = false, defaultValue = "alipay.zip", paramType = "query", dataType = "String")
    public R<String> unzipFile(@RequestParam(name = "filename", defaultValue = "alipay.zip") String filename) throws IOException {
        String fullDirPath = StringUtils.join(ABSTRACT_PATH, "/", filename);
        fileHandleService.unZip(fullDirPath, ABSTRACT_PATH+ File.separator + "unzip");

        return R.success("解压文件成功");
    }




    @ApiOperation(value = "读取目录下文件/子目录", httpMethod = "GET", response = R.class)
    @GetMapping("/getDirFiles")
    @ApiImplicitParam(name = "dirName", value = "文件目录", required = false, defaultValue = "logs", paramType = "query", dataType = "String")
    public R<List<String>> getDirFiles(@RequestParam(name = "dirName", defaultValue = "logs") String dirName) {
        String fullDirPath = StringUtils.join(ABSTRACT_PATH, "/", dirName);
        File file = new File(fullDirPath);

        listDir(file);

        return R.success(listFiles);
    }

    /**
     * 递归展示所有文件
     */
    private void listDir(File dir) {
        //递归打印所有文件和子文件夹的内容
        File[] fs = dir.listFiles();
        String blank = "\t";
        blank = StringUtils.repeat(blank, depth);
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {
                    logger.info(blank + f.getName() + "/");
                    depth++;
                    listDir(f);
                    depth--;
                } else {
                    logger.info(blank + f.getName());
                    listFiles.add(blank + f.getName());
                }
            }
        }
    }


    @ApiOperation(value = "创建默认权限的目录(失败提示不友好 mkdir mkdirs)", httpMethod = "GET", response = R.class)
    @GetMapping("/mkdirDir")
    @ApiImplicitParam(name = "dirName", value = "文件目录", required = false, defaultValue = "logs", paramType = "query", dataType = "String")
    public R<String> mkdirDir(@RequestParam(name = "dirName", defaultValue = "logs") String dirName) {
        String response = "创建成功";
        String fullDirPath = StringUtils.join(ABSTRACT_PATH, "/", dirName);
        File file = new File(fullDirPath);

        if (!file.exists()) {
            boolean result = file.mkdir();
            if (!result) {
                response = "创建失败";
            }
        }

        return R.success(response);
    }

    @ApiOperation(value = "创建默认权限的目录(Files.createDirectories 推荐)", httpMethod = "GET", response = R.class)
    @GetMapping("/createDirectory")
    @ApiImplicitParam(name = "dirName", value = "文件目录", required = false, defaultValue = "logs", paramType = "query", dataType = "String")
    public R<String> createDirectory(@RequestParam(name = "dirName", defaultValue = "logs") String dirName) throws IOException {
        String response = "创建目录";
        String fullDirPath = StringUtils.join(ABSTRACT_PATH, "/", dirName);
        Path path = Paths.get(fullDirPath);
        Path pathCreate = Files.createDirectories(path);
//        如果被创建文件夹的父文件夹不存在，就创建它
//        如果被创建的文件夹已经存在，就是用已经存在的文件夹，不会重复创建，没有异常抛出
//        如果因为磁盘IO出现异常，则抛出IOException.
        logger.info("create dir success " + pathCreate);

        return R.success(response);
    }


    @ApiOperation(value = "写个大文件, 用来测试大文件读取", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/writeBigFile")
    public R<String> writeBigFile(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        file = new File(fileFull);

        String response = "创建文件成功了";
        if (!file.exists()) {
            if (!file.createNewFile()) {
                response = "创建文件失败了";
            }
        }

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter buffwriter = new BufferedWriter(fileWriter);

        long count = 50000001L;
        for (long i = 0; i < count; i++) {
            buffwriter.write(i + "a123456789\n");

            if (i % 100000000 == 0) {
                logger.info("已写入" + i + "行");
                // 3200 0000 0000
                //
                // 每次
            }
        }
        fileWriter.close();
        logger.info("大文件已完成");
        return R.success(response);
    }


    @ApiOperation(value = "创建文件(默认权限)", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/")
    public R<String> create(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        file = new File(fileFull);
        String response = "创建文件成功了";
        if (!file.createNewFile()) {
            response = "创建文件失败了";
        }
        return R.success(response);
    }

    @ApiImplicitParam(name = "writeFileDto", value = "追加写入字符串", required = true, paramType = "body")
    @PostMapping("/writeStr")
    @ApiOperation(value = "追加写入字符串", httpMethod = "POST", response = R.class)
    public R<String> writeFileStr(@RequestBody WriteFileDto writeFileDto) {

        String content = writeFileDto.getContent();
        String fileName = ABSTRACT_PATH + "/" + writeFileDto.getFileName();

        BufferedWriter buffwriter = null;
        // 缓冲区能缓存8192个字符 满了或者close、flush之后才会进行查码表,之后再缓存在StreamEncoder的缓冲区中（8192字节）
        try {
            Writer writer = new FileWriter(fileName, true);
            buffwriter = new BufferedWriter(writer);
            buffwriter.write(content);
            buffwriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buffwriter != null) {
                try {
                    buffwriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return R.success("写内容成功");
    }

    @ApiImplicitParam(name = "writeFileDto", value = "追加写入字节流", required = true, paramType = "body")
    @PostMapping("/writeChar")
    @ApiOperation(value = "追加写入字节流", httpMethod = "POST", response = R.class)
    public R<String> writeFileChar(@RequestBody WriteFileDto writeFileDto) throws IOException {

        String content = writeFileDto.getContent();
        String fileName = ABSTRACT_PATH + "/" + writeFileDto.getFileName();

        File file = new File(fileName);

        FileOutputStream fileOutputStream = null;

        try {
            if (!file.exists()) {
                file.createNewFile();

            }
            fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return R.success("写内容成功");
    }

    @ApiOperation(value = "读文件", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "读文件", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/readLine")
    public R<List<String>> readFile(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {
        List<String> content = new ArrayList<>();
        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);


        Reader readFile = new FileReader(fileFull);
        BufferedReader reader = new BufferedReader(readFile);
        String line = "";
        while ((line = reader.readLine()) != null) {
            content.add(line);
        }

        return R.success(content);
    }

    @ApiOperation(value = "创建文件(777) 不直观", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/permission")
    public R<String> createPermission(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        file = new File(fileFull);
        // 可以在生成文件之前生效
//       boolean result =  file.setExecutable(true, false);
//        boolean result2 =        file.setReadable(true, false);
//       boolean result3 =  file.setWritable(true, false);

        if (file.exists()) {
            return R.success("文件已经存在了");
        }

        String response = "创建文件成功了";
        if (!file.createNewFile()) {
            response = "创建文件失败了";
        }
        // 必须在创建完文件之后才会生效
        boolean result = file.setExecutable(true);
        boolean result2 = file.setReadable(true);
        boolean result3 = file.setWritable(true);
        return R.success(response);
    }

    @ApiOperation(value = "修改文件权限(664) 推荐", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/changePermission")
    public R<String> changeFolderPermission(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        file = new File(fileFull);
        // 可以在生成文件之前生效


        String response = "创建文件成功了";
        if (!file.exists()) {
            if (!file.createNewFile()) {
                response = "创建文件失败了";
            }
        }

        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.OTHERS_READ);

        try {
            Path path = Paths.get(file.getAbsolutePath());
            Files.setPosixFilePermissions(path, perms);

        } catch (Exception e) {
            logger.info("Change folder " + file.getAbsolutePath() + " permission failed.", e);
        }
        return R.success(response);
    }

    @ApiOperation(value = "修改文件权限(664) 有安全隐患", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/changePermission2")
    public R<String> changeFolderPermission2(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) throws IOException {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        file = new File(fileFull);
        // 可以在生成文件之前生效


        String response = "创建文件成功了";
        if (!file.exists()) {
            if (!file.createNewFile()) {
                response = "创建文件失败了";
            }
        }

        // dirPath = /home/a aa.txt 识别不了有空格的文件名
        Runtime runtime = getRuntime();
        String cmd = "chmod 664 " + file.getAbsolutePath();

        Process process = runtime.exec(cmd);

        try {
            process.waitFor();
            int existValue = process.exitValue();
            if (existValue != 0) {
                logger.error("修改权限失败");
            }
        } catch (Exception e) {
            logger.info("Change folder " + file.getAbsolutePath() + " permission failed.", e);
        }
        return R.success(response);
    }

    @ApiOperation(value = "删文件", httpMethod = "POST", response = R.class)
    @ApiImplicitParam(name = "fileName", value = "操作的文件名", required = false, defaultValue = FILE_NAME_LOCAL, paramType = "query", dataType = "String")
    @PostMapping("/deleteFile")
    public R<String> deleteFile(@RequestParam(name = "fileName", defaultValue = FILE_NAME_LOCAL) String fileName) {

        if (StringUtils.isEmpty(fileName)) {
            fileName = FILE_NAME_LOCAL;
        }

        String fileFull = StringUtils.join(ABSTRACT_PATH, "/", fileName);
        File file = new File(fileFull);
        if (!file.exists()) {
            return R.success("不需要删除");
        }

        boolean deleteResult = file.delete();
        String response = deleteResult ? "删除成功" : "删除失败";

        return R.success(response);
    }

}
