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
    public static final String NOTIFYWXPAY = "yourself notifyurl";
    // 企业付款接口
    public static final String BUSINESSPAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    // 微信开放平台下的appid
    public static final String APPID = "appid";
    // 微信商户平台下的api密钥
    public static final String APISECRET = "apiSecret";
    // 微信商户平台的商户号
    public static final String MCHID = "mchId";
    // 微信支付所需证书路径
    public static final String KEYSTORE_URL = "yourself keystore url";
    // 微信支付订单的查询
    public static final String ORDER_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";
}
