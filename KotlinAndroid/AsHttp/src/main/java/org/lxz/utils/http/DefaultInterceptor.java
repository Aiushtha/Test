package org.lxz.utils.http;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.lxz.utils.ashttp.R;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by Lin on 2017/4/10.
 */

public class DefaultInterceptor implements AsHttpInterceptor{

    private  ErrorCodeTable errorTable;
    public Map<Class,String> mistake=new HashMap<>();
    public class ErrorCodeTable{
       public Map<String,String> htppCodeTable;
       public Map<String,String> throwsTable;
       public String defaultHttpCodeMessage;
       public String defaultThrowsMessage;
    }

    Application app;
    public DefaultInterceptor(Application application,int httpCodeRawFileRes)
    {
        app=application;
        InputStream in = app.getApplicationContext().getResources().openRawResource(httpCodeRawFileRes);
        try {
            String errorCodeMsg=read(in,"utf-8");
            errorTable = new Gson().fromJson(errorCodeMsg, ErrorCodeTable.class);
            Iterator<Map.Entry<String, String>> it = errorTable.throwsTable.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry<String, String> keyvalue = it.next();
                String claPath= keyvalue.getKey();
                String msg=keyvalue.getValue();
                Class cla=Class.forName(claPath);
                mistake.put(cla,msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public DefaultInterceptor(Application application)
    {
       this(application,R.raw.exception_filter);
    }

    @Override
    public String httpCodeMessage(int code) {
        String msg= errorTable.htppCodeTable.get(String.valueOf(code));
        return msg==null?errorTable.defaultHttpCodeMessage:msg;
    }





    @Override
    public String interceptor(Response response, String json) throws Exception {
        return json;
    }

    @Override
    public HttpException handlerThrowable(Throwable e)  {
        if(e instanceof HttpException)return (HttpException) e;
        String errorMsg=mistake.get(e.getClass());
        return new HttpException(0,errorMsg==null?errorTable.defaultThrowsMessage:errorMsg);
    }



    public static String read(InputStream in, String encode) throws IOException {

        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        if (encode == null) {
            br = new BufferedReader(new InputStreamReader(in));
        } else {
            br = new BufferedReader(new InputStreamReader(in, encode));
        }
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\n");

        }
        br.close();
        return sb.toString().trim();
    }



}
