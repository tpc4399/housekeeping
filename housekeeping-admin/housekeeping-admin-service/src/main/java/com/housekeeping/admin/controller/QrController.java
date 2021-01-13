package com.housekeeping.admin.controller;

import com.aliyun.oss.OSSClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import com.netflix.ribbon.proxy.annotation.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Author su
 * @Date 2021/1/11 10:58
 */
@Api(tags={"【二维码】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/qr")
public class QrController {

    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @ApiOperation("二维码生成")
    @GetMapping("/generateQr")
    public R generateQr(String content, HttpServletResponse response) throws IOException {
        ServletOutputStream stream = null;
        try {
            stream = response.getOutputStream();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");//编码
            hints.put(EncodeHintType.MARGIN, 0);//边框距
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bm = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints);
            MatrixToImageWriter.writeToStream(bm, "png", stream);
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        } finally {
            if (stream != null){
                stream.flush();
                stream.close();
            }
        }
        return R.ok();
    }

    @ApiOperation("二维码生成，返回链接")
    @GetMapping("/generateQr2")
    public R generateQr2(String content) throws IOException {
        ByteArrayOutputStream out = null;
        String res = "";
        try {
            //二维码字节流生成
            out = new ByteArrayOutputStream();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");//编码
            hints.put(EncodeHintType.MARGIN, 0);//边框距
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bm = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bm);
            ImageIO.write(image, "png", out);
            byte[] bytes = out.toByteArray();
            //进行oss存储
            Random random = new Random();
            Integer ra = random.nextInt(10000);
            Integer userId = TokenUtils.getCurrentUserId();
            LocalDateTime now = LocalDateTime.now();
            String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String catalogue = CommonConstants.HK_LOGIN_IN_QR_ABSTRACT_PATH_PREFIX_PROV;
            String type = ".png";
            String fileAbstractPath = catalogue + "/" + nowString + userId + ra + type;
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(bytes));
            res = urlPrefix + fileAbstractPath;

        } catch (IOException | WriterException e) {
            e.printStackTrace();
        } finally {
            if (out != null){
                out.flush();
                out.close();
            }
        }
        return R.ok(res, "轉換成功，已生成鏈接");
    }

}
