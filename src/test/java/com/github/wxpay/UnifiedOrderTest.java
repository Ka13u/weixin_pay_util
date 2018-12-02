package com.github.wxpay;

import com.github.wxpay.service.WeixinPayService;
import com.github.wxpay.utils.WXPayConfig;
import com.github.wxpay.utils.ZXingUtils;
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
        WeixinPayService service = new WeixinPayService(wxPayConfig,false);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "腾讯充值中心-QQ会员充值");
        data.put("out_trade_no", "2016090910595900000459");
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");    //单位 分
        data.put("notify_url", "http://www.example.com/wxpay/notify");
        data.put("spbill_create_ip", "123.123.123.123");
        data.put("trade_type", "NATIVE");  // 扫码支付
        data.put("product_id", "1");

        try {
            Map<String, String> resp = service.unifiedOrder(data);
            System.out.println(resp);
            /**
             * 判断返回的return_code、result_code,再进行业务逻辑操作
             */
            if(resp.get("trade_type").equals("NATIVE") && resp.get("code_url") != null){    //扫码支付生成支付二维码
                ZXingUtils.writeQrCode(resp.get("code_url"),"D:\\");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
