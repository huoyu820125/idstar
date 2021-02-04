package com.github.huoyu820125.idstar.stream;

import com.github.huoyu820125.idstar.error.RClassify;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Title WriteStream
 * @Athor SunQian
 * @CreateTime 2021/1/28 11:43
 * @Description: todo
 */
public class WriteStream {
    protected OutputStream stream;

    public WriteStream() {
    }

    public WriteStream(OutputStream stream) {
        this.stream = stream;
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
        }
    }

    /**
     * @title: 写数据
     * @author: SunQian
     * @date: 2020/11/25 18:47
     * @descritpion: 从offset开始,最多写入maxWriteSize数据
     * @param buffer   写入buffer
     * @param offset   从buffer的什么位置开始写入
     * @param maxSize  最大写入的长度，<=0时不限制，将buffer中offset开始之后的数据全部写入
     * @return 实际写入长度
     */
    public long write(byte[] buffer, int offset, long maxSize) {
        if (null == buffer) {
            throw RClassify.refused.exception("缺少参数：buffer为null");
        }
        if (offset > buffer.length - 1) {
            throw RClassify.refused.exception("参数错误：offset越界");
        }

        //计算写入长度
        int writeSize = Long.valueOf(maxSize).intValue();
        if (writeSize <= 0) {
            //将从offset(包括offset位置)开始的后续数据全部写入
            writeSize = buffer.length - offset;
        }
        if (offset + writeSize > buffer.length) {
            writeSize = buffer.length;
        }

        try {
            stream.write(buffer, offset, writeSize);
        } catch (IOException e) {
            throw RClassify.bug.exception("写数据异常", e);
        }

        return writeSize;
    }

    /**
     * @title: 将输出流中的数据写入
     * @author: SunQian
     * @date: 2021/1/28 14:06
     * @descritpion: todo
     * @param src 写入数据来源
     * @param maxSize  最大写入的长度，<=0时不限制，将buffer中offset开始之后的数据全部写入
     * @return 实际写入长度
     */
    public long write(InputStream src, long maxSize) {
        return write(new ReadStream(src, null, false), maxSize);
    }

    /**
     * @title: 将输出流中的数据写入
     * @author: SunQian
     * @date: 2021/1/28 14:06
     * @descritpion: todo
     * @param src 写入数据来源
     * @param maxSize  最大写入的长度，<=0时不限制，将buffer中offset开始之后的数据全部写入
     * @return 实际写入长度
     */
    public long write(ReadStream src, long maxSize) {
        byte[] buffer = new byte[5*1024];
        int writeSize = 5*1024;
        long len = 0;
        while (0 >= maxSize || len < maxSize) {
            if (maxSize > 0 && len + writeSize > maxSize) {
                writeSize = Long.valueOf(maxSize - len).intValue();
            }

            int n = src.read(buffer, 0, writeSize);
            if (-1 == n) {
                break;
            }
            write(buffer, 0, 0);
            len += n;
        }

        return len;
    }
}
