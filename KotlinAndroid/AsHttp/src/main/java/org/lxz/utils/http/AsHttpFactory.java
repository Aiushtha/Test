package org.lxz.utils.http;

import android.util.Log;

import com.google.gson.Gson;

import org.lxz.utils.log.L;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Lin on 2017/5/27.
 */

public class AsHttpFactory {

    public HashMap<String, HttpLog> requestHashMap = new HashMap<>();

    public AsHttp create(AsHttp asHttp) {
        asHttp.setjPath(jPath)
                .url(url);
        if (method != null) asHttp.setMethod(method);
        asHttp.okHttpClient = okHttpClient;
        asHttp.setAsHttpInterceptor(this.asHttpInterceptor);
        return asHttp;
    }

    public interface LogListen{
        void log(HttpLog httpLog);
    }
    public interface ClientBuilder{
        void build(OkHttpClient.Builder build);
    }

    protected final String getJPath() {
        return "";
    }

    protected String url="http://127.0.0.1";
    protected String jPath;
    protected OkHttpClient okHttpClient;
    protected Retrofit retrofit;
    protected int connectTimeout;
    protected int readTimeout;
    protected boolean showLog = true;
    protected String tag = "LogHttpInfo";
    protected Interceptor netCacheInterceptor;
    protected AsHttpInterceptor asHttpInterceptor;
    protected List<Interceptor> interceptorList;
    protected List<Interceptor> netInterceptorList;
    protected Map<String, String> heads;
    protected Map<String, String> parameters;
    protected AsHttp.Method method;
    protected LogListen logListen;
    private ClientBuilder clientBuilder;

    AsHttpFactory() {
    }

   public class HttpLog {
        public String number;
        public Response response;
        public Request request;
        public int code;
        public Throwable throwable;
        public String data;
        public String message;

        private void handlerLog() {
            if (showLog) {
                try {
                    message=getMessage();
                    L.json(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                if (logListen != null) {
                    logListen.log(this);
                }

        }

        public void setMessage(String str){message=str;};
        public String getMessage()  {
            if(message!=null)return message;
            LinkedHashMap<String, Object> print = null;
            try {
                print = new LinkedHashMap<>();
                MediaType mediaType=null;
                if(response!=null&&response.body()!=null) {
                    mediaType = response.body().contentType();
                }
                RequestBody requestBody = response!=null?response.request().body():request.body();
                Buffer buffer = new Buffer();
                if (requestBody != null) {
                    requestBody.writeTo(buffer);
                }
                Charset charset = Charset.forName("UTF-8");
                String paramsStr = buffer.readString(charset);
                String decParamsStr = null;
                try {
                    decParamsStr = URLDecoder.decode(paramsStr, "UTF-8");
                } catch (Exception e) {
                }
                Map<String, String> postParams = null;
                try {
                    String[] arr = decParamsStr.split("&");
                    postParams = new HashMap<>();
                    for (String str : arr) {
                        String[] keyValue = str.split("=");
                        String key = keyValue[0];
                        String value = keyValue[1];
                        postParams.put(key, value);
                    }
                } catch (Exception e) {
                }
                String url = response!=null?response.request().url().toString():request.url().toString();
                print.put("id", number);
                print.put("url", url);
                print.put("method", response!=null?response.request().method():request.method());
                print.put("mediaType", mediaType==null?null:mediaType);
                print.put("head", response!=null?response.request().headers():request.headers());
                if (url.indexOf("?") != -1) {
                    Map<String, String> getParams = new HashMap<>();
                    try {
                        String[] urlPamArr = url.split("\\?")[1].split("&");
                        for (String str : urlPamArr) {
                            String[] keyValue = str.split("=");
                            String key = keyValue[0];
                            String value = URLDecoder.decode(keyValue[1], "UTF-8");
                            getParams.put(key, value);
                        }
                        print.put("query", getParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                print.put("params", paramsStr);
                print.put("paramsDecode", postParams);


                {
                    {
                        if (request.body() instanceof MultipartBody) {
                            print.put("isMultipartBody", true);
                            MultipartBody multipartBody = (MultipartBody) request.body();
                            print.put("multipartBodyboundary", multipartBody.boundary());
                            print.put("multipartContentLength", multipartBody.contentLength());
                            print.put("multipartContentLength", multipartBody.type());
                            print.put("multipartBody", multipartBody.parts());
                            List<Map> listInfo = new ArrayList<>();
                            for (MultipartBody.Part part : multipartBody.parts()) {
                                HashMap map = new HashMap();
                                map.put("contentLength", part.body().contentLength());
                                map.put("contentType", part.body().contentType());
                                listInfo.add(map);
                            }
                            print.put("multiparContentInfo", listInfo);

                        }
                        if (request.body() instanceof FormBody) {
                            print.put("isFromBody", true);
                            FormBody oldFormBody = (FormBody) request.body();

                            List<Map> fromInfos = new ArrayList<>();
                            for (int i = 0; i < oldFormBody.size(); i++) {
                                HashMap<String, Object> fromBodyMap = new HashMap<>();
                                fromBodyMap.put("contentLength", oldFormBody.contentLength());
                                fromBodyMap.put("contentType", oldFormBody.contentType());
                                fromBodyMap.put("value", oldFormBody.value(i));
                                fromInfos.add(fromBodyMap);
                            }
                            print.put("frombody", fromInfos);
                        }
                    }
                }

                print.put("code", code);
                print.put("data", data);
                try {
                    String dataDecode = ascii2native(data);
                    print.put("dataDecode", dataDecode);
                    print.put("dataJsonDecode", new Gson().fromJson(dataDecode, Map.class));
                } catch (Exception e) {
                }

                try {
                    print.put("error", throwable == null ? throwable : (throwable.getClass().getSimpleName() + " " + throwable.getMessage()));
                } catch (Exception e) {

                }
            } catch (IOException e) {

            }
            return message=new Gson().toJson(print);


        }
    }

    public static class Build {
        public AsHttpFactory asHttpFactory = new AsHttpFactory();

        /**
         * 设置主链接的url
         */
        public Build setUrl(String url) {
            asHttpFactory.url = url;
            return this;
        }

        /**
         * jsonPath解析
         */
        public Build setJsonPath(String jsonPath) {
            asHttpFactory.jPath = jsonPath;
            return this;
        }

        /**
         * 客户端逻辑
         */
        public Build setClientLogic(AsHttpInterceptor asHttpInterceptor) {
            asHttpFactory.asHttpInterceptor = asHttpInterceptor;
            return this;
        }

        /**
         * 客户端逻辑
         */
        public Build setShowLog(boolean showLog) {
            asHttpFactory.showLog = showLog;
            return this;
        }

        /**
         * 客户端逻辑
         */
        public Build setReadTimeout(int seconds) {
            asHttpFactory.readTimeout = seconds;
            return this;
        }

        /**
         * 连接超时
         */
        public Build setConnectTimeout(int seconds) {
            asHttpFactory.connectTimeout = seconds;
            return this;
        }

        /**
         * 添加拦截器
         */
        public Build addInterceptor(Interceptor interceptor) {
            if (asHttpFactory.interceptorList == null)
                asHttpFactory.interceptorList = new ArrayList<>();
            asHttpFactory.interceptorList.add(interceptor);
            return this;
        }

        /**
         * 添加拦截器
         */
        public Build addNetInterceptor(Interceptor interceptor) {
            if (asHttpFactory.netInterceptorList == null)
                asHttpFactory.netInterceptorList = new ArrayList<>();
            asHttpFactory.netInterceptorList.add(interceptor);
            return this;
        }

        public Build addParameter(String key, String value) {
            if (asHttpFactory.parameters == null) asHttpFactory.parameters = new HashMap<>();
            asHttpFactory.parameters.put(key, value);
            return this;
        }

        public Build addHeader(String key, String value) {
            if (asHttpFactory.heads == null) asHttpFactory.heads = new HashMap<>();
            asHttpFactory.heads.put(key, value);
            return this;
        }

        public Build setMethod(AsHttp.Method method) {
            asHttpFactory.method = method;
            return this;
        }

        public Build setHttpLog(LogListen logListen){
            asHttpFactory.logListen=logListen;
            return this;
        }

        public Build setClientBuild(ClientBuilder clientBuilder){
            asHttpFactory.clientBuilder=clientBuilder;
            return this;
        }


        public AsHttpFactory build() {
            return asHttpFactory.build();
        }


    }


    public AsHttpFactory build() {
        netCacheInterceptor = createInterceptor();
        okHttpClient = createOkHttpClient();
        retrofit = createRetrofitInstance();
        return this;
    }




    public final Retrofit createRetrofitInstance() {
        return retrofit = new Retrofit.Builder()
                .client(okHttpClient)

                .addConverterFactory(new ResponseConvertFactory(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    public final OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder build = new OkHttpClient.Builder();


        if (interceptorList != null && !interceptorList.isEmpty()) {
            for (Interceptor inter : interceptorList) {
                build.addInterceptor(inter);
            }
        }
        if (netInterceptorList != null && !netInterceptorList.isEmpty()) {
            for (Interceptor inter : netInterceptorList) {
                build.addNetworkInterceptor(inter);
            }
        }
        if(clientBuilder!=null)
        {
            clientBuilder.build(build);
        }
        build.addInterceptor(netCacheInterceptor)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS);


        return build.build();
    }



    public final Interceptor createInterceptor() {
        return netCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                HttpLog httpLog = new HttpLog();
                Response response = null;
                try {
                    Request request = chain.request();


                    HttpUrl httpUrl;
                    HttpUrl.Builder httpUrlBuild = request.url().newBuilder();
                    if (parameters != null) {
                        Iterator<Map.Entry<String, String>> it = parameters.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, String> keyValue = it.next();
                            httpUrlBuild.addQueryParameter(keyValue.getKey(), keyValue.getValue());
                        }

                    }
                    httpUrl = httpUrlBuild.build();

                    Request.Builder build = request.newBuilder();
                    Headers buildHeads;
                    if (heads != null) {
                        buildHeads = Headers.of(heads);
                        build.headers(buildHeads);
                    }
                    httpLog.request = request;

                    response = chain.proceed(build.url(httpUrl).build());
                    httpLog.response = response;
                    httpLog.code = response.code();
                    httpLog.number = response.body().source().buffer().sha1().hex();
                    requestHashMap.put(httpLog.number, httpLog);


                    if (response.code() != 200) {
                        requestHashMap.remove(httpLog.number);
                        throw new HttpException(httpLog.code, "http code");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    requestHashMap.remove(httpLog.number);
                    httpLog.throwable = e;
                    if (asHttpInterceptor != null) {
                        HttpException httpException = asHttpInterceptor.handlerThrowable(e);
                        httpLog.throwable = httpException;
                        httpLog.handlerLog();
                        throw httpException;
                    }else{
                        httpLog.handlerLog();
                    }

                }
                return response;

            }
        };
    }


    public final class ResponseConvertFactory extends Converter.Factory {
        public ResponseConvertFactory create() {
            return create(new Gson());
        }

        public ResponseConvertFactory create(Gson gson) {
            return new ResponseConvertFactory(gson);
        }

        private ResponseConvertFactory(Gson gson) {
            if (gson == null) throw new NullPointerException("gson == null");
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(final Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody value) throws IOException {
                    String sha1 = value.source().buffer().sha1().hex();
                    HttpLog httpLog = null;
                    if (value != null) {
                        httpLog = requestHashMap.get(sha1);
                    }
                    try {
                        String str = value.string();
                        if (httpLog != null) httpLog.data = str;
                        if(asHttpInterceptor!=null) {
                            String jsonResult = asHttpInterceptor.interceptor(httpLog.response, str);
                            return JPathGson.fromJson(jsonResult, jPath, type);
                        }else {
                            return JPathGson.fromJson(str, jPath, type);
                        }
                    } catch (Exception e) {
                        if (httpLog != null) {
                            httpLog.throwable = e;
                        }
                        if (asHttpInterceptor != null) {
                            throw asHttpInterceptor.handlerThrowable(e);
                        } else {
                            throw new RuntimeException(e);
                        }
                    } finally {
                        if (httpLog != null) {
                            requestHashMap.remove(httpLog.number);
                            httpLog.handlerLog();
                        }
                    }

                }
            };
        }

    }


    public String getUrl() {
        return url;
    }

    public AsHttpFactory setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getjPath() {
        return jPath;
    }

    public AsHttpFactory setjPath(String jPath) {
        this.jPath = jPath;
        return this;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public AsHttpFactory setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        return this;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public AsHttpFactory setRetrofit(Retrofit retrofit) {
        this.retrofit = retrofit;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public AsHttpFactory setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public AsHttpFactory setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public AsHttpFactory setShowLog(boolean showLog) {
        this.showLog = showLog;
        return this;
    }

    public Interceptor getNetCacheInterceptor() {
        return netCacheInterceptor;
    }

    public AsHttpFactory setNetCacheInterceptor(Interceptor netCacheInterceptor) {
        this.netCacheInterceptor = netCacheInterceptor;
        return this;
    }

    public AsHttpInterceptor getAsHttpInterceptor() {
        return asHttpInterceptor;
    }

    public AsHttpFactory setAsHttpInterceptor(AsHttpInterceptor asHttpInterceptor) {
        this.asHttpInterceptor = asHttpInterceptor;
        return this;
    }

    public List<Interceptor> getInterceptorList() {
        return interceptorList;
    }

    public AsHttpFactory setInterceptorList(List<Interceptor> interceptorList) {
        this.interceptorList = interceptorList;
        return this;
    }

    public List<Interceptor> getNetInterceptorList() {
        return netInterceptorList;
    }

    public AsHttpFactory setNetInterceptorList(List<Interceptor> netInterceptorList) {
        this.netInterceptorList = netInterceptorList;
        return this;
    }

    public Map<String, String> getHeads() {
        return heads;
    }

    public AsHttpFactory setHeads(Map<String, String> heads) {
        this.heads = heads;
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public AsHttpFactory setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

//    public String getHttpNumber(BufferedSource resp) {
//        L.d("resp:-->"+resp.getClass()+" "+resp);
//        try {
//            String a = resp.toString();
//            String regEx = "@{1}[a-zA-Z0-9_]++";
//            Pattern p = Pattern.compile(regEx);
//            Matcher m = p.matcher(a);
//            while (m.find()) {
//                return m.group(0);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }


    private static String ascii2native(String asciicode) {
        String[] asciis = asciicode.split("\\\\u");
        String nativeValue = asciis[0];
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
                if (code.length() > 4) {
                    nativeValue += code.substring(4, code.length());
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }


}
