package com.rogue.pay.demo.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author weigaosheng
 * @description
 * @CalssName StringUtil
 * @date 2019/2/20
 * @params
 * @return
 */
public class StringUtil {
    //随机字符串生成
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = UUID.randomUUID()+ TimeUtil.getCurrentDateString();
        base = base.replaceAll("-","");
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
