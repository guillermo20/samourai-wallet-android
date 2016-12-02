package com.samourai.wallet.util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

public class SendAddressUtil {

    private static SendAddressUtil instance = null;

    private static HashMap<String,Boolean> sendAddresses = null;

    private SendAddressUtil() { ; }

    public static SendAddressUtil getInstance() {

        if(instance == null) {
            sendAddresses = new HashMap<String,Boolean>();
            instance = new SendAddressUtil();
        }

        return instance;
    }

    public void add(String addr, boolean showAgain) {
        sendAddresses.put(addr, showAgain);
    }

    public int get(String addr) {
        if(sendAddresses.get(addr) == null) {
            return -1;
        }
        else if (sendAddresses.get(addr) == true) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public JSONArray toJSON() {

        JSONArray sent_tos = new JSONArray();
        for(String key : sendAddresses.keySet()) {
            JSONArray sent = new JSONArray();
            sent.put(key);
            sent.put(sendAddresses.get(key));
            sent_tos.put(sent);
        }

        return sent_tos;
    }

   public void fromJSON(JSONArray tos) {
        try {
            for(int i = 0; i < tos.length(); i++) {
                JSONArray sent = (JSONArray)tos.get(i);
                sendAddresses.put((String)sent.get(0), (boolean)sent.get(1));
            }
        }
        catch(JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

}
