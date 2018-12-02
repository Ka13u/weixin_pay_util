package com.github.wxpay.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * Created by KaBu on 2018/12/1.
 */
@Component
public class WXPayConfig{

    private byte[] certData;

    @Value("${wx.pay.appID}")
    private String appID;

    @Value("${wx.pay.key}")
    private String key;

    @Value("${wx.pay.mchID}")
    private String mchID;

    @Value("${wx.pay.httpConnectTimeoutMs}")
    private int httpConnectTimeoutMs;

    @Value("${wx.pay.httpReadTimeoutMs}")
    private int httpReadTimeoutMs;

    @Value("${wx.pay.certPath}")
    private String certPath;


    public WXPayConfig() throws Exception {
        if(certPath != null && !certPath.isEmpty()){
            File file = new File(certPath);
            InputStream certStream = new FileInputStream(file);
            certData = new byte[(int) file.length()];
            certStream.read(certData);
            certStream.close();
        }
    }

    public String getAppID() {
        return appID;
    }

    public String getMchID() {
        return mchID;
    }

    public String getKey() {
        return key;
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

}
