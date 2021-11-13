package service;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Service {

    /*
    *       PASSWORD ACCOUNT DB:
    *   admin1@email.com  -> Admin1
    *   client1@email.com -> Client1
    *   client2@email.com -> Client2
    *
    * */

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

    public static void setError(JSONObject json, String error) {
        try {
            json.put("done", false);
            json.put("error", error);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
