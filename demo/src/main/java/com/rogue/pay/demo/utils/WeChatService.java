package com.rogue.pay.demo.utils;

import com.rogue.pay.demo.exception.GlobalException;
import com.rogue.pay.demo.result.CodeMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jdom2.JDOMException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
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
@Slf4j
@Service
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

    /**
     * @return a
     * @Author weigaosheng
     * @Description 微信支付回调方法
     * @Description 参考API：https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_7&index=3
     * @Date 16:33 2018/12/2
     * @Param
     **/
    public String notifyWeiXinPay(HttpServletRequest request) {
        log.info("-----微信支付回调-----");
        InputStream inStream = null;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            String resultxml = new String(outSteam.toByteArray(), "utf-8");
            Map<String, Object> params = PayCommonUtil.doXMLParse(resultxml);
            outSteam.close();
            inStream.close();

            if (!PayCommonUtil.isTenpaySign(params)) {//验证签名失败处理
                log.info("-----验证签名失败-----" + params);
                String result = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]></return_msg>" + "</xml> ";
                return result;
            }
            /*
             * 处理回调结果
             * */
            if ("SUCCESS".equals(params.get("return_code"))) {
                if ("SUCCESS".equals(params.get("result_code"))) {
                    String payTime = params.get("time_end").toString();//20141030133525
                    String payFlow = params.get("transaction_id").toString();//微信支付订单号-即流水编号
                    String totalFee = params.get("total_fee").toString();//总金额
                    String goodsNo = params.get("out_trade_no").toString();//商品订单号
                    String openId = params.get("openid").toString();//支付微信号
//                    String isSubscribe = params.get("is_subscribe").toString();//该微信是都关注app公众号
//                    String tradeType = params.get("trade_type").toString();//交易类型
//                    String bankType = params.get("bank_type").toString();//付款银行
//                    String feeType = params.get("fee_type").toString();//货币种类
                    // 处理自己业务逻辑
                } else {
                    // 处理自己业务逻辑
                    log.info("支付失败，失败编码：{}，失败描述：{}", params.get("result_code").toString(), params.get("err_code_des").toString());

                }
            } else {
                log.info("支付失败？错误信息：{}", params.get("return_msg"));
            }
            //拼装xml信息返回微信端接收到支付回调结果
            String result = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            return result;
        } catch (Exception e) {
            log.info("-----微信手机支付失败-----" + e.getMessage());
            String result = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            return result;
        }
    }

    /**
     * @return
     * @Author weigaosheng
     * @Description 企业支付接口
     * @Date 17:29 2018/12/27
     * @Param partner_trade_no 商户订单号
     * @Param openid 用户openid
     * @Param re_user_name 用户真实姓名 如果真实姓名校验不通过出现提现失败
     * @Param amount 金额
     * @Param desc 企业付款备注 例如：理赔
     * @Param spbill_create_ip Ip地址 该IP可传用户端或者服务端的IP
     **/
    public Map<String, Object> businessPrePay(String partner_trade_no, String openid, String re_user_name,
                                              BigDecimal amount, String desc, HttpServletRequest request) {
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
        //接口中参数支付金额单位为【分】，参数值不能带小数，所以乘以100
        BigDecimal payAmount = amount.multiply(new BigDecimal(100));
        parameterMap.put("mch_appid", WeChatUtil.APPID);
        parameterMap.put("mchid", WeChatUtil.MCHID);
        parameterMap.put("nonce_str", StringUtil.getRandomString(32));
        parameterMap.put("partner_trade_no", partner_trade_no);
        parameterMap.put("openid", openid);
        parameterMap.put("check_name", "NO_CHECK");//不进行强制校验真实姓名
        if(StringUtils.isNotEmpty(re_user_name) && !"NO_CHECK".equals(parameterMap.get("check_name"))){
            parameterMap.put("re_user_name", re_user_name );
        }
        parameterMap.put("amount", payAmount.toString());
        parameterMap.put("desc", desc);
        parameterMap.put("spbill_create_ip", PayCommonUtil.getIpAdrress(request));
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);

        Map<String, Object> map = null;
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        try {
            String result = PayCommonUtil.httpsRequest2(
                    WeChatUtil.BUSINESSPAY, requestXML);
            // 解析xml格式为map
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            //JDOME解析异常
            throw new GlobalException(CodeMsg.WECHAT_ERROR);
        } catch (IOException e) {
            //io异常
            throw new GlobalException(CodeMsg.IO_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * @return
     * @Author weigaosheng
     * @Description 微信支付订单查询接口
     * @Date 17:29 2018/12/27
     * @Param 微信订单号	transaction_id 或者 商户订单号 out_trade_no （二选一）
     **/
    public Map<String, Object> wechatOrderQuery(String transaction_id, String out_trade_no) {
        SortedMap<String, Object> parameterMap = new TreeMap<String, Object>();
        parameterMap.put("appid", WeChatUtil.APPID);
        parameterMap.put("mch_id", WeChatUtil.MCHID);
        if(StringUtils.isNotEmpty(transaction_id)){
            parameterMap.put("transaction_id", transaction_id);
        }
        if(StringUtils.isNotEmpty(out_trade_no)){
            parameterMap.put("out_trade_no", out_trade_no);
        }
        parameterMap.put("nonce_str", StringUtil.getRandomString(32));
        String sign = PayCommonUtil.createSign("UTF-8", parameterMap);
        parameterMap.put("sign", sign);

        Map<String, Object> map = null;
        String requestXML = PayCommonUtil.getRequestXml(parameterMap);
        try {
            String result = PayCommonUtil.httpsRequest(
                    WeChatUtil.ORDER_QUERY, "POST", requestXML);
            // 解析xml格式为map
            map = PayCommonUtil.doXMLParse(result);
        } catch (JDOMException e) {
            //JDOME解析异常
            throw new GlobalException(CodeMsg.WECHAT_ERROR);
        } catch (IOException e) {
            //io异常
            throw new GlobalException(CodeMsg.IO_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
