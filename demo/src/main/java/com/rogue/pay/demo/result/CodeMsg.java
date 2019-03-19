package com.rogue.pay.demo.result;

/**
 * @author weigaosheng
 * @description
 * @CalssName CodeMsg
 * @date 2019/2/28
 * @params
 * @return
 */
public class CodeMsg {
    private int code;
    private String msg;

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500102, "登录超时，请重新登录");
    public static CodeMsg IO_EXCEPTION = new CodeMsg(500103, "IO异常");

    // 登录模块5002XX
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500200, "密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500201, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500202, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500203, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500204, "输入密码错误");

    // 微信模块5003XX
    public static CodeMsg WECHAT_ERROR = new CodeMsg(500300, "微信数据解析异常");


    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArsg(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

}
