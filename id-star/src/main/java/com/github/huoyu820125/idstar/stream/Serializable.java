package com.github.huoyu820125.idstar.stream;

/**
 * 大端序(网络序)序列化
 * @author SunQian
 * @version 1.1
 */
public class Serializable {
    public static byte[] getStream(Integer num) {
        byte[] byteStream = new byte[4];
        int i = 0;
        for (i = 0; i < 4; i++) {
            int offset = 32 - (i + 1) * 8;
            byteStream[i] = (byte) ((num >> offset) & 0xff);
        }
        return byteStream;
    }

    public static int getInteger(byte[] byteStream) {
        int i = 0;
        int num = 0;
        for (i = 0; i < 4; i++) {
            num <<= 8;
            num |= (byteStream[i] & 0xff);
        }
        return num;
    }

    public static byte[] getStream(Long num) {
        byte[] byteStream = new byte[8];
        int i = 0;
        for (i = 0; i < 8; i++) {
            int offset = 64 - (i + 1) * 8;
            byteStream[i] = (byte) ((num >> offset) & 0xff);
        }
        return byteStream;
    }

    public static long getLong(byte[] byteStream) {
        int i = 0;
        long num = 0;
        for (i = 0; i < 8; i++) {
            num <<= 8;
            num |= (byteStream[i] & 0xff);
        }
        return num;
    }

}
