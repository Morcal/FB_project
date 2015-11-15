package com.feibo.joke.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringUtil {
    public static int byteLength(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = s.charAt(i);
            if (ascii >= 0 && ascii <= 127) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    public static String subString(String string, String suffix, int length) {
        if (length < 5) {
            return "";
        }
        length -= byteLength(suffix);
        int l = 0;
        for (int i = 0; i < string.length(); i++) {
            int c = string.charAt(i);
            if (c >= 0 && c <= 127) {
                l += 1;
            } else {
                l += 2;
            }
            if (l >= length - 3) {
                return string.substring(0, i - 1) + "..." + suffix;
            }
        }
        return string + suffix;
    }

    public static String getPictureFormat(String path) {
        String format = "";
        format = path.substring(path.length() - 3, path.length());
        return format;
    }

    public static String urlEncode(String detailUrl) {
        int index = detailUrl.indexOf("url=");
        if (index != -1) {
            String subForth = detailUrl.substring(0, index);
            String temp = detailUrl.substring(index);
            return subForth + temp.replace('&', '$');
        }
        return "";
    }

    public static boolean isEmpty(String text) {
        return text == null || text.equals("");
    }
    
    public static String encode2Utf(String s) {
        String text = null;
        try {
            text = URLEncoder.encode(s , "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
//            ToastUtil.showSimpleToast("评论内容含非法字符哦！");
        }
        return text;
    }
}
