package org.lxz.utils.http;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

import org.lxz.utils.log.L;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lin on 2017/4/1.
 */

public class JPathGson {

    public static <T> T fromJson(String json,String jpath,Class<T> cla){
        String nodeJson;
        if(jpath==null)
        {
            nodeJson=json;
        }else {
            nodeJson = JsonPath.parse(json).read(jpath).toString();
        }
        L.d(nodeJson);
        return getGson().fromJson(nodeJson,cla);
    }
    public static <T> T fromJson(String json,String jpath,Type cla){
        Object nodeJson;
        if(jpath==null)
        {
            return getGson().fromJson(json,cla);
        }else {
            nodeJson = JsonPath.parse(json).read(jpath);
        }
        return getGson().fromJson(getGson().toJson(nodeJson),cla);
    }
    public static <T> List<T> fromJsonList(String json, String jpath, Class<T> cla){
        String nodeJson=JsonPath.parse(json).read(jpath).toString();
        List<T> list=new ArrayList<T>();
        return getGson().fromJson(nodeJson, list.getClass());
    }

    public static Gson getGson(){
        return new Gson();
    }

    public static String readJsonPath(String json, String jPath) {
        if(jPath==null||"".equals(jPath)||"$.".equals(jPath))return json;
        return JsonPath.parse(json).read(jPath).toString();
    }
}
