package com.rogue.pay.demo.utils;

/**
 * @author weigaosheng
 * @description
 * @CalssName WeChatUtil
 * @date 2019/2/20
 * @params
 * @return
 */
public class WeChatUtil {
    // 统一下单请求地址
    public static final String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    // 微信支付回调地址
    public static final String NOTIFYWXPAY = "youself notifyurl";
    // 微信开放平台下的appid
    public static final String APPID = "appid";
    // 微信商户平台下的api密钥
    public static final String APISECRET = "apiSecret";
    // 微信商户平台的商户号
    public static final String MCHID = "mchId";
}
