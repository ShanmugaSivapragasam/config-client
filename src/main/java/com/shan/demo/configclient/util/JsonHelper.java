package com.shan.demo.configclient.util;


import org.json.JSONObject;

public class JsonHelper {

    public  static JSONObject createJsonObject(String jsonString)  {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject;
    }
}
