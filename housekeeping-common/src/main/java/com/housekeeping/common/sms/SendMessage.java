package com.housekeeping.common.sms;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import org.json.JSONException;

import javax.xml.ws.http.HTTPException;

/**
 * @Author su
 * @create 2020/10/29 14:54
 */
public class SendMessage{

    public static void sendMessage(String nationCode, String phoneNumber, String[] params){
        // 短信应用SDK AppID
        int appid = 1400096409; // 你的AppID 1400开头

        // 短信应用SDK AppKey
        String appkey = "fc1c7e21ab36fef1865b0a3110709c51";

        // 短信模板ID，需要在短信应用中申请
        int templateId = 331300; //短信模板的ID NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
        //templateId7839对应的内容是"您的验证码是: {1}"
        // 签名
        String smsSign = "三次猿"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

        try {
            //数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            //86 中国大陆 默认值
            //852 中国香港
            //886 中国台湾
            //853 中国澳门
            if (nationCode == null || "".equals(nationCode))
                nationCode = "86";
            SmsSingleSenderResult result = ssender.sendWithParam(nationCode, phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    public static void sendWorkStartMessage(String nationCode, String phoneNumber, String[] params){
        // 短信应用SDK AppID
        int appid = 1400096409; // 你的AppID 1400开头

        // 短信应用SDK AppKey
        String appkey = "fc1c7e21ab36fef1865b0a3110709c51";

        // 短信模板ID，需要在短信应用中申请
        int templateId = 953774; //短信模板的ID NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
        //templateId7839对应的内容是"您的验证码是: {1}"
        // 签名
        String smsSign = "三次猿"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

        try {
            //数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            //86 中国大陆 默认值
            //852 中国香港
            //886 中国台湾
            //853 中国澳门
            if (nationCode == null || "".equals(nationCode))
                nationCode = "86";
            SmsSingleSenderResult result = ssender.sendWithParam(nationCode, phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    public static void sendWorkEndMessage(String nationCode, String phoneNumber, String[] params){
        // 短信应用SDK AppID
        int appid = 1400096409; // 你的AppID 1400开头

        // 短信应用SDK AppKey
        String appkey = "fc1c7e21ab36fef1865b0a3110709c51";

        // 短信模板ID，需要在短信应用中申请
        int templateId = 953781; //短信模板的ID NOTE: 这里的模板ID`7839`只是一个示例，真实的模板ID需要在短信控制台中申请
        //templateId7839对应的内容是"您的验证码是: {1}"
        // 签名
        String smsSign = "三次猿"; // NOTE: 这里的签名"腾讯云"只是一个示例，真实的签名需要在短信控制台中申请，另外签名参数使用的是`签名内容`，而不是`签名ID`

        try {
            //数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            //86 中国大陆 默认值
            //852 中国香港
            //886 中国台湾
            //853 中国澳门
            if (nationCode == null || "".equals(nationCode))
                nationCode = "86";
            SmsSingleSenderResult result = ssender.sendWithParam(nationCode, phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (Exception e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String[] arr = {"建明"};
        sendWorkEndMessage("86","13528438232",arr);
    }
}
