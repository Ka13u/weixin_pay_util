package com.github.wxpay;

import com.github.wxpay.utils.WXPayConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by KaBu on 2018/12/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigTest {

    @Autowired
    private WXPayConfig wxPayConfig;

    @Test
    public void testConfig(){
        System.out.println("");
        System.out.println("AppID=" + wxPayConfig.getAppID());
        System.out.println("");
    }


}
