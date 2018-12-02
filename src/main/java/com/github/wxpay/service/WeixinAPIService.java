package com.github.wxpay.service;

import com.github.wxpay.utils.WXPayConfig;
import com.github.wxpay.utils.WXPayConstants;
import com.github.wxpay.utils.WXPayConstants.SignType;
import com.github.wxpay.utils.WXPayRequest;
import com.github.wxpay.utils.WXPayUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by KaBu on 2018/11/30.
 */
@Component
public class WeixinAPIService {


    private WXPayConfig config;
    private SignType signType;
    private boolean useSandbox;
    private String notifyUrl;
    private WXPayRequest wxPayRequest;

    public WeixinAPIService() {
    }

    public WeixinAPIService(final WXPayConfig config) throws Exception {
        this(config, null, false);
    }

    public WeixinAPIService(final WXPayConfig config, final boolean useSandbox) throws Exception{
        this(config, null, useSandbox);
    }

    public WeixinAPIService(final WXPayConfig config, final String notifyUrl) throws Exception {
        this(config, notifyUrl, false);
    }

    public WeixinAPIService(final WXPayConfig config, final String notifyUrl, final boolean useSandbox) throws Exception {
        this.config = config;
        this.notifyUrl = notifyUrl;
        this.useSandbox = useSandbox;
        if (useSandbox) {
            this.signType = SignType.MD5; // 沙箱环境
        }
        else {
            this.signType = SignType.HMACSHA256;
        }
        this.wxPayRequest = new WXPayRequest(config);
    }

    /**
     * 作用：统一下单接口
     * @param reqData 请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData) throws Exception {
        checkWXPayConfig();
        return this.unifiedOrder(reqData, config.getHttpConnectTimeoutMs(), this.config.getHttpReadTimeoutMs());
    }

    /**
     * 作用：统一下单
     * @param reqData 请求数据
     * @param connectTimeoutMs 连接超时时间，单位毫秒
     * @param readTimeoutMs 读超时时间，单位毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> unifiedOrder(Map<String, String> reqData,  int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String url;
        if (this.useSandbox) {
            url = WXPayConstants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
        }
        else {
            url = WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
        }
        if(this.notifyUrl != null) {
            reqData.put("notify_url", this.notifyUrl);
        }
        String respXml = this.requestWithoutCert(url, this.fillRequestData(reqData), connectTimeoutMs, readTimeoutMs);
        return this.processResponseXml(respXml);
    }

    /**
     * 不需要证书的请求
     * @param urlSuffix url前缀
     * @param reqData 请求数据
     * @param connectTimeoutMs 超时时间，单位毫秒
     * @param readTimeoutMs 超时时间，单位毫秒
     * @return API返回数据
     * @throws Exception
     */
    public String requestWithoutCert(String urlSuffix, Map<String, String> reqData,
                                     int connectTimeoutMs, int readTimeoutMs) throws Exception {
        String msgUUID = reqData.get("nonce_str");
        String reqBody = WXPayUtil.mapToXml(reqData);

        String resp = this.wxPayRequest.requestWithoutCert(urlSuffix, msgUUID, reqBody, connectTimeoutMs, readTimeoutMs);
        return resp;
    }


    /**
     * 向 Map 中添加 appid、mch_id、nonce_str、sign_type、sign
     * @param reqData
     * @return
     * @throws Exception
     */
    public Map<String, String> fillRequestData(Map<String, String> reqData) throws Exception {
        reqData.put("appid", config.getAppID());
        reqData.put("mch_id", config.getMchID());
        reqData.put("nonce_str", WXPayUtil.generateNonceStr());
        if (SignType.MD5.equals(this.signType)) {
            reqData.put("sign_type", WXPayConstants.MD5);
        }else if (SignType.HMACSHA256.equals(this.signType)) {
            reqData.put("sign_type", WXPayConstants.HMACSHA256);
        }
        reqData.put("sign", WXPayUtil.generateSignature(reqData, config.getKey(), this.signType));
        return reqData;
    }


    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        }
        else if (return_code.equals(WXPayConstants.SUCCESS)) {
            if (this.isResponseSignatureValid(respData)) {
                return respData;
            }
            else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        }
        else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }


    /**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     * @param reqData 请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
        // 返回数据的签名方式和请求中给定的签名方式是一致的
        return WXPayUtil.isSignatureValid(reqData, this.config.getKey(), this.signType);
    }



    private void checkWXPayConfig() throws Exception {
        if (this.config == null) {
            throw new Exception("config is null");
        }
        if (this.config.getAppID() == null || this.config.getAppID().trim().length() == 0) {
            throw new Exception("appid in config is empty");
        }
        if (this.config.getMchID() == null || this.config.getMchID().trim().length() == 0) {
            throw new Exception("mchid in config is empty");
        }
        if (this.config.getHttpConnectTimeoutMs() < 10) {
            throw new Exception("http connect timeout is too small");
        }
        if (this.config.getHttpReadTimeoutMs() < 10) {
            throw new Exception("http read timeout is too small");
        }
    }
}
