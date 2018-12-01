package com.github.wxpay;

import com.github.wxpay.service.WeixinAPIService;
import com.github.wxpay.utils.WXPayConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KaBu on 2018/12/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UnifiedOrderTest{

    @Autowired
    private WXPayConfig wxPayConfig;

    @Test
    public void testOrder() throws Exception {
        WeixinAPIService service = new WeixinAPIService(wxPayConfig,true);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "腾讯充值中心-QQ会员充值");
        data.put("out_trade_no", "2016090910595900000012");
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");    //单位 分
        data.put("spbill_create_ip", "123.123.123.123");
        data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
        data.put("product_id", "1");

        try {
            Map<String, String> resp = service.unifiedOrder(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
