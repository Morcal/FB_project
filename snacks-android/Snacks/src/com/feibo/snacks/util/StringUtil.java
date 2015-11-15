package com.feibo.snacks.util;

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

    public static int getCharacterLength(String nick) {
        int length = 0;
        for (int i = 0; i < nick.length(); i++) {
            if (Character.isLetter(nick.charAt(i))) {
                length++;
            }
        }
        return length;
    }

    public static int getChineseLength(String nick) {
        int length = 0;
        for (int i = 0; i < nick.length(); i++) {
            int temp = nick.charAt(i);
            if (temp >= 19968 && temp <= 171941) {
                length++;
            }
        }
        return length;
    }
}
