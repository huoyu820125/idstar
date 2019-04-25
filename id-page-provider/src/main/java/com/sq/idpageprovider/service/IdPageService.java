package com.sq.idpageprovider.service;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author sq
 * @version 1.0
 * @className IdPageService
 * @description TODO
 * @date 2019/4/24 下午4:31
 */
@Service
public class IdPageService {
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * 最大页码
     * 页码占36bit
     */
    private static long maxPageNo = 0xfffffffL;

    @Value("${data.path}")
    private String dataPath;

    /**
     * 空闲页
     *
     * @param
     * @return java.lang.Long 页码
     * @author sq
     * @date 2019/4/24 下午4:32
     */
    public Long idle(Integer version) {
        if (version < 0 || version > 255) {
            throw new RuntimeException("invalid version: version must be between 0 and 255");
        }

        String dataFileName = "nextPageNo.v" + version.toString();
        Long pageNo = 0L;
        synchronized (IdPageService.class) {
            String fileFullPath = getJarFullPath() + File.separator + dataPath;
            String fileFullPathName = fileFullPath + File.separator + dataFileName;

            Resource resource = new FileSystemResource(fileFullPathName);
            File dataFile = null;
            try {
                if (!resource.exists()) {
                    dataFile = new File(fileFullPath, dataFileName);
                    FileUtils.touch(dataFile);
                }

                //读下一个空闲页码
                resource = new FileSystemResource(fileFullPathName);
                dataFile = resource.getFile();
                FileInputStream iutputStream = new FileInputStream(dataFile);
                byte[] stream = new byte[8];
                iutputStream.read(stream);
                iutputStream.close();
                pageNo = bytes2Long(stream);
                if (maxPageNo == pageNo) {
                    throw new RuntimeException("no resources: arrived last page");
                }

                //下一个空闲页码写回文件
                Long nextPageNo = pageNo + 1;
                FileOutputStream outputStream = new FileOutputStream(dataFile);
                stream = long2Bytes(nextPageNo);
                outputStream.write(stream);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return pageNo;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteStream = new byte[8];
        int i = 0;
        for (i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            byteStream[i] = (byte) ((num >> offset) & 0xff);
        }
        return byteStream;
    }

    public static long bytes2Long(byte[] byteStream) {
        int i = 0;
        long num = 0;
        for (i = 0; i < 8; i++) {
            num <<= 8;
            num |= (byteStream[i] & 0xff);
        }
        return num;
    }

    /**
     * 取得jar全路径
     *
     * @param
     * @return java.lang.String
     * @author mc
     * @date 2019/4/24 下午8:24
     */
    private String getJarFullPath() {
//            实际执行时work_path + jar_path就是jar绝对路径
//            String work_path = new File("").getAbsolutePath();
//            logger.info("工作路径(IDE调试下是ide工作目录，真实运行时是java指令执行时当前目录)="+work_path);
//            String jar_path = System.getProperty("java.class.path");
//            logger.info("jar相对路径(IDE调试下不可用，真实运行时是jar相对路径)="+jar_path);

        String jarFullPath = IdPageService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//            logger.info("jar绝对路径(IDE调试下是不带target/class位置，实际运行时是jar绝对路径+file:前缀)="+jarFullPath);

        if (!jarFullPath.contains(".jar")) {
            //ide中调试，取到的是class目录，target下
            int endPos = jarFullPath.indexOf("class");
            if (-1 != endPos) {
                jarFullPath = jarFullPath.substring(0, endPos - 1);
            } else {
                endPos = jarFullPath.indexOf(File.separator);
                jarFullPath = jarFullPath.substring(0, endPos);
            }
        } else {
            //真实运行
            int endPos = jarFullPath.indexOf(".jar");
            int lastPos = 0;
            int startPos = jarFullPath.indexOf(File.separator, 0);
            jarFullPath = jarFullPath.substring(startPos);
            startPos = jarFullPath.indexOf(File.separator, 0);
            while (startPos < endPos) {
                lastPos = startPos;
                startPos = jarFullPath.indexOf(File.separator, startPos + 1);
            }
            endPos = lastPos;
            jarFullPath = jarFullPath.substring(0, endPos);
        }

        return jarFullPath;
    }
}
