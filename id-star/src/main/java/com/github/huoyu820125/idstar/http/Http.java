package com.github.huoyu820125.idstar.http;

import com.github.huoyu820125.idstar.http.core.BodyInputStream;
import com.github.huoyu820125.idstar.http.core.HttpClient;
import com.github.huoyu820125.idstar.IdStar;
import com.github.huoyu820125.idstar.error.RClassify;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * http操作api
 * @author SunQian
 * @version 2.0
 */
public class Http {
    private static Logger log = LoggerFactory.getLogger(IdStar.class);

    private Map<String, Object> uriParams;
    private Map<String, List<String>> headers = new HashMap<>();
    private HttpEntity body;
    private HttpRequestBase request;
    private HttpResponse response;
    private BodyInputStream bodyStream;

    /******************************设置参数******************************/
    //收到回应结果后自动关闭链接，默认不关闭，保持长连接
    private Boolean autoClose = false;
    //数据编码格式（包括但不限于uri参数，body数据）
    private Charset charset = StandardCharsets.UTF_8;

    /**
     * 收到回应结果后自动关闭链接
     * @author: SunQian
     * @param autoClose
     * @return todo
     */
    public Http autoClose(boolean autoClose) {
        this.autoClose = autoClose;

        return this;
    }

    /**
     * 设置编码格式
     * @author: SunQian
     * @param charset
     * @return todo
     */
    public Http charset(Charset charset) {
        this.charset = charset;

        return this;
    }


    /**
     * 添加uri参数
     *  uri?uriParam=uriValue
     * @author: SunQian
     * @param name  参数名
     * @param value 参数值
     * @return todo
    */
    public Http addUriParam(String name, Object value) {
        if (null == uriParams) {
            uriParams = new HashMap<>();
        }

        if (uriParams.containsKey(name)) {
            throw RClassify.param.exception("重复的uri参数：" + name);
        }

        uriParams.put(name, value);

        return this;
    }

    /**
     * 添加header参数
     * @author: SunQian
     * @param name
     * @param value
     * @return todo
    */
    public Http addHeader(String name, String value) {
        List<String> values = headers.get(name);
        if (null == values) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(value);

        return this;
    }

    /**
     * 设置body数据：json类型数据
     * @author: SunQian
     * @param jsonStr   body内容json字符串
     * @return todo
    */
    public Http setBody(String jsonStr) {
        byte[] buf = jsonStr.getBytes(charset);
        ByteArrayInputStream stream = new ByteArrayInputStream(buf);
        body = new InputStreamEntity(stream, buf.length, ContentType.APPLICATION_JSON);

//        addHeader("Content-Type", "application/json;charset=utf8");
//        addHeader("Content-length", String.valueOf(buf.length));

        return this;
    }

    /**
     * 设置body数据：表单类型数据
     * @author: SunQian
     * @param formData
     * @return todo
    */
    public Http setBody(Map<String, List<Object>> formData) {
        List<NameValuePair> forms = new ArrayList<>();
        for (Map.Entry<String, List<Object>> entry : formData.entrySet()) {
            for (Object value : entry.getValue()) {
                if (!(value instanceof String)) {
                    throw RClassify.param.exception("目前版本只支持string类型值");
                }
                forms.add(new BasicNameValuePair(entry.getKey(), (String) value));
            }
        }

        body = new UrlEncodedFormEntity(forms, charset);

        return this;
    }

    /**
     * 发起post请求
     * @author: SunQian
     * @param url       类似http://www.xxx.com/net_path
     * @param timeout   超时时间(ms)
     * @return todo
    */
    public Http post(String url, int timeout) {
        URI uri = uri(url, uriParams);

        HttpPost request = new HttpPost(uri);
        this.request = request;
        if (timeout > 0) {
            RequestConfig tRequestConfig = RequestConfig.copy(HttpClient.config()).setSocketTimeout(timeout).build();
            request.setConfig(tRequestConfig);
        }

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            for(String value : header.getValue()) {
                request.addHeader(header.getKey(), value);
            }
        }

        if (null != body) {
            request.setEntity(body);
        }

        HttpContext httpContext = new BasicHttpContext();
        HttpResponse resp = null;
        try {
            response = HttpClient.instance().execute(request, httpContext);
        } catch (IOException e) {
            throw RClassify.bug.exception("http请求异常:" + url + "", e);
        }

        return this;
    }

    public Http get(String url, int timeout) {
        if (null != body) {
            throw RClassify.refused.exception("发现有待提交的body，get方法不支持body数据提交");
        }

        URI uri = uri(url, uriParams);

        HttpGet request = new HttpGet(uri);
        this.request = request;
        if (timeout > 0) {
            RequestConfig tRequestConfig = RequestConfig.copy(HttpClient.config()).setSocketTimeout(timeout).build();
            request.setConfig(tRequestConfig);
        }

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            for(String value : header.getValue()) {
                request.addHeader(header.getKey(), value);
            }
        }

        HttpContext httpContext = new BasicHttpContext();
        try {
            response = HttpClient.instance().execute(request, httpContext);
        } catch (IOException e) {
            throw RClassify.bug.exception("http请求异常:" + url + "", e);
        }

        return this;
    }

    private URI uri(String url, Map<String, Object> paramMap) {
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw RClassify.param.exception("url 错误: " + url);
        }
        if (null == paramMap || paramMap.isEmpty()) {
            return uri;
        }

        StringBuilder queryString = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (i > 0) {
                queryString.append("&");
            }
            queryString.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
            i++;
        }

        try {
            return new URI(
                    uri.getScheme(),
                    uri.getUserInfo(),
                    uri.getHost(),
                    uri.getPort(),
                    uri.getPath(),
                    queryString.toString(),
                    uri.getFragment());
        } catch (URISyntaxException e) {
            throw RClassify.param.exception("url queryString错误: " + queryString);
        }
    }

    /*****************************************接收回应结果******************************************/
    private Boolean ok() {
        if (200 == response.getStatusLine().getStatusCode()) {
            return true;
        }

        return false;
    }

    /**
     * 接收回应数据
     * @author: SunQian
     * @return todo
    */
    public String response() {
        byte[] body = null;
        try {
            body = IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IOException e) {
            throw RClassify.bug.exception("读取http响应结果异常", e);
        }
        finally {
            if (autoClose) {
                close();
            }
        }
        String jsonStr = new String(body, charset);

        if (!ok()) {
            throw RClassify.bug.exception(jsonStr);
        }

        return jsonStr;
    }

    /**
     * 接收回应数据
     * @author: SunQian
     * @param clazz 回应数据类型
     * @return todo
    */
    public Object response(Class<?> clazz) {
        String response = response();
        try {
            if (Integer.class.equals(clazz)) {
                return Integer.valueOf(response);
            }
            if (Long.class.equals(clazz)) {
                return Long.valueOf(response);
            }
            if (Short.class.equals(clazz)) {
                return Long.valueOf(response);
            }
            if (Byte.class.equals(clazz)) {
                return Long.valueOf(response);
            }
            if (Character.class.equals(clazz)) {
                return Long.valueOf(response);
            }
            if (Boolean.class.equals(clazz)) {
                if (response.equals("true")) {
                    return true;
                }
                else if (response.equals("false")) {
                    return false;
                }
            }
            throw RClassify.bug.exception("http响应类型错误：期望类型：" + clazz.getName() + "，响应数据:" + response);
        } catch (Exception e) {
            throw RClassify.bug.exception("http响应类型错误：期望类型：" + clazz.getName() + "，响应数据:" + response);
        }
    }

    /**
     * 取body数据流
     * @author: SunQian
     * @return todo
    */
    public BodyInputStream bodyStream() {
        if (null != bodyStream) {
            return bodyStream;
        }

        synchronized (this) {
            if (null != bodyStream) {
                return bodyStream;
            }
            bodyStream = new BodyInputStream(request, response, autoClose);
            if (autoClose) {
                request = null;
                response = null;
            }
        }

        return bodyStream;
    }

    /**
     * 关闭链接
     * @author: SunQian
     * @return todo
    */
    public void close() {
        if (null == request) {
            return;
        }

        try {
            // 尝试断开连接，不抛出错误
            request.releaseConnection();
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
        } finally {
            request = null;
            response = null;
        }
    }
}
