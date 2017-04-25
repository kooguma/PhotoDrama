package com.loopeer.android.photodrama4android.api.sign;

import android.net.Uri;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SignUtils {

    /**
     * Encodes characters in the given string as '%'-escaped octets
     * using the UTF-8 scheme. Leaves letters ("A-Z", "a-z"), numbers
     * ("0-9"), and unreserved characters ("_-!.~'()*") intact. Encodes
     * all other characters.
     * the "_-." encode by the rawurlencode
     * or null if s is null
     */
    public static String apiEncryptSign(IdentityHashMap<String, Object> map, String md5key) {
        List<Map.Entry<String,Object>> mapList = new ArrayList(map.entrySet());
        Collections.sort(mapList, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> mapping1, Map.Entry<String, Object> mapping2) {
                return mapping1.getKey().compareTo(mapping2.getKey());
            }
        });
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> mapping : mapList) {
            sb.append(mapping.getKey() + "=" + mapping.getValue().toString());
        }
        sb.append(md5key);
        String encodeString;//
        String string = sb.toString();
        encodeString = Uri.encode(string, "utf-8");
        String newEncodeString = replaceUnreservedChar(encodeString);
        String signString = null;
        try {
            signString = md5Encode(newEncodeString);
        } catch (Exception e) {
            //
        }
        return signString;
    }

    private static String replaceUnreservedChar(String string) {
        string = string.replace("!", "%21");
        string = string.replace("~", "%7E");
        string = string.replace("'", "%27");
        string = string.replace("(", "%28");
        string = string.replace(")", "%29");
        string = string.replace("*", "%2A");
        return string;
    }

    /***
     * MD5加密 生成32位md5码
     *
     * @param
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
