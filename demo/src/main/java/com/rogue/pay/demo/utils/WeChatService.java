package com.rogue.pay.demo.utils;

import org.jdom2.JDOMException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author weigaosheng
 * @description
 * @CalssName WeChatService
 * @date 2019/2/20
 * @params
 * @return
 */
public class WeChatService {

    /**
     * @param desc     商品描述
     * @param orderNum 商户订单号
     * @param request
     * @return a
     * @Author weigaosheng
     * @Description
     * @Date 11:04 2018/12/2
     * @Param totalPrice 总金额
     **/
    public SortedMap<String, Object> wxpay(HttpServletRequest request, BigDecimal totalPrice, String orderNum, String desc) {

        String trade_no = "";
        try {
            trade_no = new String(orderNum.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SortedMap<String, Object> map = weixinPrePay(trade_no, totalPrice, desc, request);
        return map;
    }

    /**
     * 统一下单
     * 应用场景：商户系统先调用该接口在微信支付服务后台生成预支付交易单，返回正确的预支付交易回话标识后再在APP里面调起支付。
     * @param totalAmount  总金额
     * @param description  商品描述
     * @param out_trade_no 商户订单号
     * @param request
     * @return
     */
    public SortedMap<String, Object> weixinPrePay(String out_trade_no, BigDecimal totalAmount,
                                                  String description, HttpServletRequest request) {
        String noceStr = StringUtil.getRandomString(32);
        SortedMap<String, Object> parameterMap = new TreeMap<>();
        parameterMap.put("appid", WeChatUtil.APPID);
        parameterMap.put("body", description);
        parameterMap.put("mch_id", WeChatUtil.MCHID);
        parameterMap.put("nonce_str", noceStr);
        parameterMap.put("notify_url", WeChatUtil.NOTIFYWXPAY);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("spbill_create_ip", PayCommonUtil.getIpAdrress(request));
        // 接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        BigDecimal total = totalAmount.multiply(new BigDecimal(100));
        java.text.DecimalFormat df = new java.text.DecimalFormat("0");
        parameterMap.put("total_fee", df.format(total));
        parameterMap.put("trade_type", "APP");
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        //请求预付下单
        String result = PayCommonUtil.httpsRequest(WeChatUtil.URL, "POST", requestXML);
        Map<String, Object> map = null;
        SortedMap<String, Object> secondMap = new TreeMap<>();
        try {
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ("SUCCESS".equals(map.get("return_code"))) {
            if ("SUCCESS".equals(map.get("result_code"))) {
                // 二次签名开始
                // 此处的参数放置的顺序一定要这样排列！
                secondMap.put("appid", WeChatUtil.APPID);
                secondMap.put("noncestr", map.get("nonce_str"));
                secondMap.put("package", "Sign=WXPay");
                secondMap.put("partnerid", WeChatUtil.MCHID);
                secondMap.put("prepayid", map.get("prepay_id"));
                secondMap.put("timestamp", Long.toString(System.currentTimeMillis() / 1000));
                String secondSign = PayCommonUtil.createSign("UTF-8", secondMap);
                secondMap.put("sign", secondSign);

            } else {
                secondMap.put("result", "error");
            }
        } else {
            secondMap.put("result", "error");
        }
        return secondMap;
    }
}
