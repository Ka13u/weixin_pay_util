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

    public IWXPayDomain getWXPayDomain() { // 初始化域名
        IWXPayDomain iwxPayDomain = new IWXPayDomain() {
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }
            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
        return iwxPayDomain;
    }

    /**
     * 是否自动上报。
     * 若要关闭自动上报，子类中实现该函数返回 false 即可。
     * @return
     */
    public boolean shouldAutoReport() {
        return true;
    }

    /**
     * 进行健康上报的线程的数量
     * @return
     */
    public int getReportWorkerNum() {
        return 6;
    }


    /**
     * 健康上报缓存消息的最大数量。会有线程去独立上报
     * 粗略计算：加入一条消息200B，10000消息占用空间 2000 KB，约为2MB，可以接受
     * @return
     */
    public int getReportQueueMaxSize() {
        return 10000;
    }

    /**
     * 批量上报，一次最多上报多个数据
     *
     */
    public int getReportBatchSize() {
        return 10;
    }

}
