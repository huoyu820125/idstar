package com.github.huoyu820125.idregion.service;

import com.github.huoyu820125.idstar.IdStarConfig;
import com.github.huoyu820125.idstar.error.RClassify;
import com.github.huoyu820125.idstar.file.DiskFile;
import com.github.huoyu820125.idstar.stream.ReadStream;
import com.github.huoyu820125.idstar.stream.WriteStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
 * @className IdRegionService
 * @description  分布式全局唯一区号结构：结点id(高位2byte)+本地流水号(低位n byte)，2 + n < regionNoLen
 * @date 2019/4/24 下午4:31
 */
@Service
public class IdRegionService implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${idStart.idStruct.snLen:16}")
    protected Integer snLen;
    @Value("${idStart.idStruct.raceNoLen:6}")
    protected Integer raceNoLen;
    @Value("${idStart.idStruct.regionNoLen:41}")
    protected Integer regionNoLen;
    private IdStarConfig idStarConfig;

    @Value("${data.path}")
    private String dataDir;

    @Value("${cluster.node.id:1}")
    private Long nodeId;

    private Boolean inited = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (regionNoLen <= 2) {
            throw RClassify.refused.exception("配置项idStart.idStruct.regionNoLen必须>2");
        }

        idStarConfig = new IdStarConfig(snLen, raceNoLen, regionNoLen);
    }

    public void init(Integer nodeId) {
        if (null == nodeId) {
            throw new RuntimeException("缺少结点id");
        }
        if (1 > nodeId || nodeId > 4) {
            throw new RuntimeException("结点id只能是1~4");
        }

        synchronized(this) {
            if (inited) {
                throw new RuntimeException("初始化已完成，不能设置结点id");
            }
            this.nodeId = nodeId.longValue();
            if (nodeId < 1 || nodeId > 4) {
                throw new RuntimeException("cluster.node.id:最少1个节点, 最多4个节点");
            }
            saveNodeId(nodeId);

            this.nodeId--;
            this.nodeId = this.nodeId << (idStarConfig.getRegionNoLen() - 2);
            inited = true;
            log.info("初始化完成：nodeId={}", nodeId);
        }
    }

    public Integer readNodeId() {
        String path = getJarFullPath() + File.separator + dataDir;
        DiskFile file = new DiskFile(path + File.separator + "node.sav");
        if (!file.exists()) {
            return null;
        }

        ReadStream readStream = file.startRead(false);
        Integer nodeId = null;
        try {
            nodeId = readStream.readInteger();
        } finally {
            readStream.close();
        }

        log.info("读取到结点id:{}", nodeId);

        return nodeId;
    }

    private void saveNodeId(Integer nodeId) {
        String path = getJarFullPath() + File.separator + dataDir;
        DiskFile file = new DiskFile(path + File.separator + "node.sav");
        WriteStream writeStream = file.startWrite();
        try {
            writeStream.write(nodeId);
        } finally {
            writeStream.close();
        }
    }

    public Boolean isInited() {
        return inited;
    }

    /**
     * 空闲地区
     *
     * @param
     * @return java.lang.Long 无人区的区号
     * @author sq
     * @date 2019/4/24 下午4:32
     */
    public Long idle(Integer version) {
        if (version < 0 || version > idStarConfig.getMaxRaceNo()) {
            throw new RuntimeException("invalid version: version must be between 0 and 255");
        }

        String dataFileName = "nextRegionNo.v" + version.toString();
        //分布式唯一区号=结点id(2byte) + 本地未使用的流水号(n byte)
        Long serialNo = 0L;
        long regionNo = 0;
        synchronized (IdRegionService.class) {
            String fileFullPath = getJarFullPath() + File.separator + dataDir;
            String fileFullPathName = fileFullPath + File.separator + dataFileName;

            Resource resource = new FileSystemResource(fileFullPathName);
            File dataFile = null;
            try {
                if (!resource.exists()) {
                    dataFile = new File(fileFullPath, dataFileName);
                    FileUtils.touch(dataFile);
                }

                //读未使用的流水号
                resource = new FileSystemResource(fileFullPathName);
                dataFile = resource.getFile();
                FileInputStream iutputStream = new FileInputStream(dataFile);
                byte[] stream = new byte[8];
                iutputStream.read(stream);
                iutputStream.close();
                serialNo = bytes2Long(stream);
                regionNo = nodeId + serialNo;
                if (idStarConfig.getMaxRegionNo() == regionNo) {
                    throw new RuntimeException("no resources: arrived last region");
                }

                //未使用的流水号写回文件
                serialNo++;
                FileOutputStream outputStream = new FileOutputStream(dataFile);
                stream = long2Bytes(serialNo);
                outputStream.write(stream);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return regionNo;
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
//            log.info("工作路径(IDE调试下是ide工作目录，真实运行时是java指令执行时当前目录)={}", work_path);
//            String jar_path = System.getProperty("java.class.path");
//            log.info("jar相对路径(IDE调试下不可用，真实运行时是jar相对路径)={}", jar_path);

        String jarFullPath = IdRegionService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        jarFullPath = jarFullPath.replace("/", File.separator);
//        log.info("jar绝对路径(IDE调试下是不带target/class位置，实际运行时是jar绝对路径+file:前缀)={}", jarFullPath);

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
