package com.github.huoyu820125.idstar.stream;

import com.github.huoyu820125.idstar.error.RClassify;

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
    protected Long offset;
    private boolean authClose = false;

    public ReadStream() {
    }

    public ReadStream(InputStream stream, Long length, boolean authClose) {
        this.stream = stream;
        this.length = length;
        this.authClose = authClose;
        this.offset = 0L;
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
        }
    }

    /**
     * @title: 跳多少长度，开始读取
     * @author: SunQian
     * @date: 2020/11/25 18:57
     * @descritpion: todo
     * @param length  期望跳过长度
     * @return 实际跳过长度
     */
    public long skip(long length) throws IOException {
        if (0 >= length) {
            return 0;
        }

        long len = stream.skip(length);
        offset += len;

        return len;
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
    public int read(byte[] buffer, int offset, int readSize) {
        if (null == buffer) {
            throw RClassify.refused.exception("buffer为null");
        }
        if (0 == buffer.length) {
            throw RClassify.refused.exception("buffer空间不能为0");
        }
        if (offset > buffer.length - 1) {
            throw RClassify.refused.exception("offset越界");
        }
        if (readSize <= 0) {
            throw RClassify.refused.exception("期望读取长度必须>0");
        }

        if (offset + readSize > buffer.length) {
            readSize = buffer.length;
        }

        int sum = 0;
        while (readSize > 0) {
            int real = 0;
            try {
                real = stream.read(buffer, offset, readSize);
            } catch (IOException e) {
                throw RClassify.bug.exception("读数据异常", e);
            }
            if (-1 == real) {
                break;
            }
            offset += real;
            readSize -= real;
            sum += real;
        }
        if (0 == sum) {
            if (authClose) {
                close();
            }
            return -1;
        }

        return offset;
    }

}
