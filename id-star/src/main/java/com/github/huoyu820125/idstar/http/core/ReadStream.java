package com.github.huoyu820125.idstar.http.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Title 读取流
 * @Athor SunQian
 * @CreateTime 2020/11/25 18:40
 * @Description: todo
 */
public class ReadStream {
    protected InputStream stream;
    protected Long length;

    public ReadStream() {
    }

    public ReadStream(InputStream stream, Long length) {
        this.stream = stream;
        this.length = length;
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
        }
    }

    /**
     * @title: 跳到什么位置开始读取
     * @author: SunQian
     * @date: 2020/11/25 18:57
     * @descritpion: todo
     * @param offset
     * @return todo
    */
    public void skip(long offset) throws IOException {
        stream.skip(offset);
    }

    /**
     * @title: 读数据
     * @author: SunQian
     * @date: 2020/11/25 18:47
     * @descritpion: 当buffer写满时,或数据读完时,停止读取
     * @param buffer    写入buffer
     * @param offset    从buffer的什么位置开始写入
     * @param readSize  希望读取的长度
     * @return 实际读到的数据长度，无数据可读时返回-1
    */
    public int read(byte[] buffer, int offset, int readSize) throws IOException {
        if (null == buffer) {
            throw new RuntimeException("buffer为null");
        }
        if (0 == buffer.length) {
            throw new RuntimeException("buffer空间不能为0");
        }
        if (offset > buffer.length - 1) {
            throw new RuntimeException("offset越界");
        }
        if (readSize <= 0) {
            throw new RuntimeException("期望读取长度必须>0");
        }

        if (offset + readSize > buffer.length) {
            readSize = buffer.length;
        }

        int sum = 0;
        while (readSize > 0) {
            int real = stream.read(buffer, offset, readSize);
            if (-1 == real) {
                break;
            }
            offset += real;
            readSize -= real;
            sum += real;
        }
        if (0 == sum) {
            return -1;
        }

        return offset;
    }

}
