package com.github.wxpay;

import com.github.wxpay.service.WeixinPayService;
import com.github.wxpay.utils.WXPayConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KaBu on 2018/12/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderQueryTest {

    @Autowired
    private WXPayConfig wxPayConfig;


    @Test
    public void testOrderQuery() throws Exception {
        WeixinPayService service = new WeixinPayService(wxPayConfig,false);

        Map<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", "2016090910595900000459");

        try {
            Map<String, String> resp = service.orderQuery(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
