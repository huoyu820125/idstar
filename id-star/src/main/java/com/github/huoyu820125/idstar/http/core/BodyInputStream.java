package com.github.huoyu820125.idstar.http.core;


import com.github.huoyu820125.idstar.http.Http;
import com.github.huoyu820125.idstar.error.RClassify;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Title BodyInputStream
 * @Athor SunQian
 * @CreateTime 2020/12/24 15:44
 * @Description: todo
 */
public class BodyInputStream extends ReadStream {
    private HttpRequestBase request;
    private HttpResponse response;
    private boolean closeByEnd;

    public BodyInputStream(HttpRequestBase request, HttpResponse response) {
        this(request, response, true);
    }

    /**
     * @title: BodyInputStream
     * @author: SunQian
     * @date: 2020/12/24 16:14
     * @descritpion: todo
     * @param request    请求对象
     * @param response    回应对象
     * @param closeByEnd    读取完成关闭流和http链接
     * @return todo
    */
    public BodyInputStream(HttpRequestBase request, HttpResponse response, boolean closeByEnd) {
        super();
        this.closeByEnd = closeByEnd;

        this.request = request;
        this.response = response;
        try {
            this.stream = response.getEntity().getContent();
        } catch (IOException e) {
            throw RClassify.bug.exception("取body数据流异常", e);
        }
        this.length = null;
    }

    /**
     * @title: 读数据
     * @author: SunQian
     * @date: 2020/12/24 15:59
     * @descritpion: 当buffer写满时,或数据读完时,停止读取
     * @param buffer    写入buffer
     * @param offset    从buffer的什么位置开始写入
     * @param readSize  希望读取的长度
     * @return 实际读到的数据长度，无数据可读时返回-1
     */
    public int read(byte[] buffer, int offset, int readSize) {
        int n = 0;
        try {
            n = super.read(buffer, offset, readSize);
        } catch (IOException e) {
            throw RClassify.bug.exception("接收body异常", e);
        }
        if (-1 == n) {
            if (closeByEnd) {
                close();
                try {
                    // 尝试断开连接，不抛出错误
                    request.releaseConnection();
                    EntityUtils.consume(response.getEntity());
                } catch (Exception e) {
                }
            }
        }

        return n;
    }
}
