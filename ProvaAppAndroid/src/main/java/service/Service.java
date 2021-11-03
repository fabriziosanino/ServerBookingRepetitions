package service;

import org.apache.commons.codec.digest.DigestUtils;

public class Service {
    public static String encryptMD5(String password) {
        String key = DigestUtils.md5Hex(password).toUpperCase();
        return key;
    }

    public static boolean checkMD5(String passwordDB, String password) {
        if(passwordDB.equals(encryptMD5(password).toUpperCase()))
            return true;
        else
            return false;
    }
}
