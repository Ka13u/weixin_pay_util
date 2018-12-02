package com.github.wxpay.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by KaBu on 2018/12/2.
 * 生产二维码码工具类
 */
public class ZXingUtils {


    /**
     * 生成二维码
     * @param contents  二维码内容
     * @param outputPath  输出地址
     * @throws Exception
     */
    public static void writeQrCode(String contents,String outputPath) throws Exception {

        int width = 300;
        int height = 300;
        String format = "png";

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // hints.put(EncodeHintType.MAX_SIZE, 350);//设置图片的最大值
        // hints.put(EncodeHintType.MIN_SIZE, 100);//设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的空度，非负数

        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,BarcodeFormat.QR_CODE, width, height,hints);

        // 生成二维码
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        Path path = Paths.get(outputPath + "/" + fileName + "." + format);
        MatrixToImageWriter.writeToPath(bitMatrix,format,path);
    }

}
