package com.housekeeping.admin.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.housekeeping.admin.service.EmployeesDetailsService;
import com.housekeeping.admin.service.ManagerDetailsService;
import com.housekeeping.common.utils.CommonConstants;
import com.housekeeping.common.utils.QrCodeUtils;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private final EmployeesDetailsService employeesDetailsService;
    @Resource
    private final ManagerDetailsService managerDetailsService;
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

    @ApiOperation("二维码生成，返回链接(type:1为经理 2为员工)")
    @GetMapping("/generateQr3")
    public R generateQr3(@RequestParam String content,
                         @RequestParam Integer typeId,
                         @RequestParam Integer id) throws IOException {
        ByteArrayOutputStream out = null;
        String res = "";
        try {
            String destPath = "/Qrcode/id.png";
            String impPath = "/head.jpg";
            String logoPath = null;

            if(typeId == 1){
                logoPath = managerDetailsService.getById(id).getHeadUrl();
            }
            if(typeId == 2){
                logoPath = employeesDetailsService.getById(id).getHeadUrl();
            }

            if(StringUtils.isEmpty(logoPath)){
                QrCodeUtils.encode(content,"",destPath,true);
            }else {
                String s = logoPath.replaceAll("https://test-live-video.oss-cn-shanghai.aliyuncs.com/", "");

                ossClient.getObject(new GetObjectRequest(bucketName, s), new File(impPath));

                QrCodeUtils.encode(content,impPath,destPath,true);

            }

            File file = new File(destPath);
            //进行oss存储
            Random random = new Random();
            Integer ra = random.nextInt(10000);
            Integer userId = TokenUtils.getCurrentUserId();
            LocalDateTime now = LocalDateTime.now();
            String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String catalogue = CommonConstants.HK_LOGIN_IN_QR_ABSTRACT_PATH_PREFIX_PROV;
            String type = ".png";
            String fileAbstractPath = catalogue + "/" + nowString + userId + ra + type;
            ossClient.putObject(bucketName, fileAbstractPath, file);
            res = urlPrefix + fileAbstractPath;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null){
                out.flush();
                out.close();
            }
        }
        return R.ok(res, "轉換成功，已生成鏈接");
    }

    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(String url,HttpServletResponse response) throws IOException {

        BufferedInputStream bis = null;
        OutputStream toClient = null;

        String s = url.replaceAll("https://test-live-video.oss-cn-shanghai.aliyuncs.com/", "");
        OSSObject object = ossClient.getObject(bucketName, s);
        try {
            //*获取ossObject的流*
            bis = new BufferedInputStream(object.getObjectContent(),512);

            //获取oss端文件的文件名，用作于下载文件的名称
            String[] arr = object.getKey().split("/");
            String fileName = arr[arr.length - 1];

            response.reset();
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            //处理文件名为中文的情况
            response.setHeader("Content-Disposition","attach;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));

            //*处理oss文件流传输*
            int number;
            byte[] buffer = new byte[512];
            while ((number = bis.read(buffer)) != -1){
                toClient.write(buffer,0,number);
            }
            toClient.flush();
            toClient.close();
        }finally {
            if(toClient != null){
                toClient.close();
            }
            if(bis != null){
                bis.close();
            }
        }
    }

}
