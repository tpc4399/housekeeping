package com.housekeeping.admin.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.housekeeping.admin.dto.EmployeesJobsDTO;
import com.housekeeping.common.utils.R;
import com.netflix.ribbon.proxy.annotation.Http;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/1/11 10:58
 */
@Api(tags={"【二维码】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/qr")
public class QrController {

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

}
