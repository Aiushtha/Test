package org.lxz.utils.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jayway.jsonpath.JsonPath;
//import com.lsxiao.apollo.core.Apollo;

import org.lxz.utils.log.D;
import org.lxz.utils.log.L;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Connection;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Lin on 2017/3/29.
 */

public class AsHttp {
    private static Context context;
    public static void initContext(Context context) {
        AsHttp.context = context;
    }
    private static AsHttpFactory defaultFactory;
    /**初始化默认工厂*/
    public static void initDefaultAsHttpFactory(AsHttpFactory factory) {
        AsHttp.defaultFactory = factory;
    }
    public enum Method {GET, POST, PUT, HEAD, DELETE, OPTIONS}
    public OkHttpClient okHttpClient;
    private Request.Builder requestBuilder;
    private FormBody.Builder requestBody;
    /**请求类型*/
    private Method method = Method.GET;
    /**拦截器*/
    private AsHttpInterceptor asHttpInterceptor;
    /**路径*/
    private String jPath;
    /**地址*/
    private String baseUrl;
    /**最终请求地址*/
    private String requestUrl;
    /**如果有测试数据，默认使用测试数据*/
    private String testData;
    /**表单提交*/
    private MultipartBody.Builder multipartBodyBuilder;
    /**OKhttp拦截器*/
    private okhttp3.Interceptor okHttpInterceptor;

    private AsHttpFactory mAsHttpfactory;

    /**retrofit*/
    public <T> T retrofit(Class<T> cla){
        if(mAsHttpfactory==null){return new AsHttpFactory().build().getRetrofit().create(cla);}
        else {
            return mAsHttpfactory.getRetrofit().create(cla);
        }
    }

    public static <T> AsHttp create() {
        if (null == context) throw new RuntimeException("AsHttp must be init");
        if (defaultFactory != null) {
            return create(defaultFactory);
        } else {
            return instance();
        }
    }


    public static <T> AsHttp create(AsHttpFactory factory) {
        if (null == context) throw new RuntimeException("AsHttp must be init");
        AsHttp asHttp=new AsHttp(factory);
        return factory.create(asHttp);
    }

    public static <T> AsHttp instance() {
        if (null == context) throw new RuntimeException("AsHttp must be init");
        return new AsHttp();
    }

    private void initOkhttpClient(){
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new AsHttpFactory.LoggerInterceptor("TAG"))
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }
    private AsHttp() {
        initOkhttpClient();
        requestBuilder = new Request.Builder();
        requestBody = new FormBody.Builder();
    }
    private AsHttp(AsHttpFactory factory) {
        mAsHttpfactory=factory;
        okHttpClient=mAsHttpfactory.getOkHttpClient();
        requestBuilder = new Request.Builder();
        requestBody = new FormBody.Builder();
    }

    public final static MediaType MEDIATYPE_TEXT_HTML = MediaType.parse("text/html");//text/html;HTML格式
    public final static MediaType MEDIATYPE_TEXT_PLAIN = MediaType.parse("text/plain");//text/plain;纯文本格式
    public final static MediaType MEDIATYPE_TEXT_XML = MediaType.parse("text/xml");//text/xml;XML格式
    public final static MediaType MEDIATYPE_TEXT_GIF = MediaType.parse("image/gif");//image/gif;gif图片格式
    public final static MediaType MEDIATYPE_TEXT_JPEG = MediaType.parse("image/jpeg");//image/jpeg;jpg图片格式
    public final static MediaType MEDIATYPE_TEXT_PNG = MediaType.parse("image/png");//image/png;png图片格式

    public final static MediaType MEDIATYPE_XHTML = MediaType.parse("application/xhtml+xml");//application/xhtml+xml;XHTML格式
    public final static MediaType MEDIATYPE_XML = MediaType.parse("application/xml");//application/xml;XML数据格式
    public final static MediaType MEDIATYPE_ATOM = MediaType.parse("application/atom+xml");//application/atom+xml;Atom XML聚合格式
    public final static MediaType MEDIATYPE_JSON = MediaType.parse("application/json");//application/json;JSON数据格式
    public final static MediaType MEDIATYPE_PDF = MediaType.parse("application/pdf");//application/pdf;pdf格式
    public final static MediaType MEDIATYPE_MSWORD = MediaType.parse("application/msword");//application/msword;Word文档格式
    public final static MediaType MEDIATYPE_STREAM = MediaType.parse("application/octet-stream");//application/octet-stream ： 二进制流数据（如常见的文件下载）
    public final static MediaType MEDIATYPE_FROM = MediaType.parse("application/x-www-form-urlencoded");//application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）


    public AsHttp addUrlScheme(String scheme){
        this.baseUrl=this.baseUrl+scheme;
        return this;
    }

    public AsHttp addFile(MediaType mediaType, String name, File file) {
        addFile(mediaType, name, file.getName(), file);
        return this;
    }

    public AsHttp addFile(String mediaType, String name, File file) {
        addFile(MediaType.parse(mediaType), name, file.getName(), file);
        return this;
    }

    public AsHttp addFile(String mediaType, String name, String fileName, File file) {
        addFile(MediaType.parse(mediaType), name, fileName, file);
        return this;
    }

    /**只支持POST方法*/
    public AsHttp addFile(MediaType mediaType, String name, String fileName, final File file) {


        if (multipartBodyBuilder == null) {
            multipartBodyBuilder = new MultipartBody.Builder();
            multipartBodyBuilder
                    .setType(MultipartBody.FORM);
        }
        RequestBody fileBody = RequestBody.create(mediaType, file);
        upFileResult.length += file.length();
        upFileResult.progress = upFileResult.length;
        multipartBodyBuilder
                .addFormDataPart(name, fileName, createCustomRequestBody(fileBody, file, new ProgressListener() {
                    @Override
                    public void onProgress(long length, long progress, boolean isComple) {
                        upFileResult = new UpFileResult();
                        upFileResult.length = length;
                        upFileResult.progress = progress;
                        upFileResult.isComple = isComple;
                        if (subscriber != null) subscriber.onNext(upFileResult);
                    }
                }));
        return this;
    }


//    public static <T> ObservableTransformer<T, T> transformer(String tag) {
//        return transformer(tag, tag);
//    }

//    public static <T> ObservableTransformer<T, T> transformer(final String susscessTag, final String errorTag) {
//        return new ObservableTransformer<T, T>() {
//
//            @Override
//            public ObservableSource<T> apply(Observable<T> upstream) {
//                upstream = upstream
//                        .unsubscribeOn(Schedulers.io())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread());
//                upstream.subscribe(new ApolloSubscribe(susscessTag, errorTag));
//                return upstream;
//
//            }
//        };
//    }
    public static Observer emptyObservable(){return EMPTYOBSERVABLE;};
    public final static Observer EMPTYOBSERVABLE=new Observer() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Object o) {

        }


        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

//    public static void apploObservable(Observable observable, String susscessTag) {
//        apploObservable(observable, susscessTag, null);
//    }

//    public static void apploObservable(Observable observable, String susscessTag, String errorTag) {
//        observable
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ApolloSubscribe(susscessTag, errorTag));
//    }


    public AsHttp heads(String key, String value) {
        requestBuilder.addHeader(key, value);
        return this;
    }

    public AsHttp parms(String key, String value) {
        requestBody.add(key, value);
        return this;
    }


//    public <String>Observable<String> apolloObservable(final String tag) {
//        Observable<String> obs=convertRx(new $<String>() {});
//        obs.subscribe(getApolloBusSubscribe((java.lang.String) tag));
//        return obs;
//    }
//    public <T>Observable<T> apolloObservable(final String tag, $<T> $s) {
//        Observable<T> obs = convertRx($s);
//        try {
//
//            return obs;
//        } finally {
//            obs.subscribe(getApolloBusSubscribe(tag));
//        }
//    }
//
//    public <T> AsHttp apolloBus(final String tag, $<T> $s) {
//        convertRx($s)
//                .subscribe(getApolloBusSubscribe(tag));
//        return this;
//    }
//
//    public <T> AsHttp apolloBus(final String tag) {
//        convertRx(new $<String>(){}).subscribe(getApolloBusSubscribe(tag));
//        return this;
//    }
//
//    public <T> AsHttp apolloFileBus(final String tag, $<T> $s) {
//        convertFileRx($s).subscribe(getApolloBusSubscribe(tag));
//        return this;
//    }

//    public <T> AsHttpCall getApolloBusSubscribe(final String tag) {
//        return new AsHttpCall() {
//            @Override
//            public void onSusscess(Object o) {
//                Apollo.emit(tag, o);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Apollo.emit(tag, e);
//            }
//        };
//    }

    public AsHttp testData(String str) {
        this.testData = str;
        return this;
    }

    public AsHttp testData(int rawId) {
        InputStream in = context.getResources().openRawResource(rawId);
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            this.testData = sb.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        L.json(testData);
        return this;
    }

    public <T> Observable<T> convertRx(Type<T> convertter) {
        java.lang.reflect.Type cla = null;
        try {
            cla = getParameterizedTypes(convertter)[0];
        } catch (Exception e) {
            cla = String.class;
        }
        return build(cla);
    }




    public Observable<String> convertRx() {
        return build(String.class);
    }

    public <T> Observable<T> convertRx(Class<T> cla) {
        return build(cla);
    }

    public <T> Observable<T> convertRx(java.lang.reflect.Type cla) {
        return build(cla);
    }

    /***/
    public Flowable<String> convertRxFlowable() {
        return buildFlowable();
    }

    public <T> Observable<UpFileResult<T>> convertFileRx(Type<T> convertter) {
        java.lang.reflect.Type cla = null;
        try {
            cla = getParameterizedTypes(convertter)[0];
        } catch (Exception e) {
            cla = String.class;
        }
        return buildFile(cla);
    }

    public <T> Observable<UpFileResult<T>> convertFileRx(java.lang.reflect.Type cla) {
        return buildFile(cla);
    }

    public Flowable<UpFileResult<String>> convertFileRx() {
        return buildFile();
    }


    public <T> Observable<T> build(java.lang.reflect.Type cla) {
        return this.request(asHttpInterceptor, cla, jPath, testData);
    }


    private UpFileResult upFileResult = new UpFileResult();

    private <T> Observable<UpFileResult<T>> buildFile(java.lang.reflect.Type cla) {
        L.d(cla.toString());
        return this.requestFile(asHttpInterceptor, cla, jPath, testData);
    }

    private Flowable<UpFileResult<String>> buildFile() {

        Call call = null;
        Request.Builder requestBuilder;
        if (testData == null) {
            call = buildOkHttpCall();
        }
        return this.requestFile(asHttpInterceptor, jPath, testData);
    }


    private Flowable<String> buildFlowable() {
        Call call = null;
        if (testData != null) {
            return this.request(asHttpInterceptor);
        } else {
            call = buildOkHttpCall();
            return this.request(asHttpInterceptor);
        }
    }

    public Call getOkHttCall() {

        if(multipartBodyBuilder!=null)
        {
            if(method!=Method.POST){
                throw new RuntimeException("multipartBodyBuilder is no null,method must be post");
            }
        }
        if(okHttpInterceptor!=null)
        {
            try {
                okHttpInterceptor.intercept(new Interceptor.Chain() {
                    @Override
                    public Request request() {
                        return null;
                    }

                    @Override
                    public Response proceed(Request request) throws IOException {
                        return null;
                    }

                    @Override
                    public Connection connection() {
                        return null;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Call call = null;
        FormBody body;
        StringBuilder tempParams;
        String methodName = null;
        switch (method) {
            case GET:
                call = getCall("GET");
                break;
            case HEAD: {
                call = getCall("HEAD");
                break;
            }
            default:
                body = requestBody.build();
                requestUrl = String.format("%s", baseUrl);
                switch (method) {
                    case POST:
                        methodName = "POST";
                        break;
                    case PUT:
                        methodName = "PUT";
                        break;
                    case DELETE:
                        methodName = "DELETE";
                        break;
                    case OPTIONS:
                        methodName = "OPTIONS";
                        break;
                }

                if (multipartBodyBuilder == null) {
                    requestBuilder.url(requestUrl)
                            .method(methodName, body);
                    for (int i = 0, j = body.size(); i < j; i++) {
                        String key = body.name(i);
                        String value = body.value(i);
                    }

                } else {

                    for (int i = 0, j = body.size(); i < j; i++) {
                        String key = body.name(i);
                        String value = body.value(i);
                        multipartBodyBuilder.addFormDataPart(key, value);
                    }
                    requestBuilder.url(requestUrl)
                            .method(methodName, multipartBodyBuilder.build());


                }
                call = okHttpClient.newCall(requestBuilder.build());
                break;
        }
        Log.d("test","-->"+call);
        return call;
    }

    public Call buildOkHttpCall() {
        return getOkHttCall();
    }
    public interface ProgressListener {
        public void onProgress(long length, long progress, boolean isComple);
    }


    private Emitter subscriber;

    public class UpFileResult<T> implements Cloneable {
        private long length;
        private long progress;
        private boolean isComple;
        private File file;
        private T result;
        private HashMap<File, UpFileResult> fileMaps = new HashMap<File, UpFileResult>();

        public long getLength() {
            return length;
        }

        public UpFileResult setLength(long length) {
            this.length = length;
            return this;
        }

        public long getProgress() {
            return progress;
        }

        public UpFileResult setProgress(long progress) {
            this.progress = progress;
            return this;
        }

        public boolean isComple() {
            return isComple;
        }

        public UpFileResult setComple(boolean comple) {
            isComple = comple;
            return this;
        }

        public File getFile() {
            return file;
        }

        public UpFileResult setFile(File file) {
            this.file = file;
            return this;
        }

        public T getResult() {
            return result;
        }

        public UpFileResult setResult(T result) {
            this.result = result;
            return this;
        }

        public UpFileResult clone() {
            UpFileResult o = null;
            try {
                o = (UpFileResult) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return o;
        }
    }

    public static RequestBody createCustomRequestBody(final RequestBody body, final File file, final ProgressListener listener) {
        return new RequestBody() {


            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
//                    sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 8192)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private Call getCall(String method) {
        FormBody body = requestBody.build();
        StringBuilder tempParams = new StringBuilder();
        for (int i = 0, j = body.size(); i < j; i++) {
            try {
                tempParams.append(String.format("%s=%s", body.name(i), URLEncoder.encode(body.value(i), "utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        requestUrl = String.format("%s?%s", baseUrl, tempParams.toString());
        requestBuilder.url(requestUrl)
                .method(method, null);
        return okHttpClient.newCall(requestBuilder.build());
    }


    public <T> Observable<UpFileResult<T>> requestFile(final AsHttpInterceptor interceptor, final java.lang.reflect.Type cla, final String jPath, final String testData) {
        return Observable.create(new ObservableOnSubscribe<UpFileResult<T>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<UpFileResult<T>> subscriber) throws Exception {
                AsHttp.this.subscriber = subscriber;
                try {
                    String json = null;
                    Response response = null;
                    if (testData == null) {
                        Response rs = buildOkHttpCall().execute();
                        response = rs.networkResponse();
                        json = rs.body().string();
                        if (interceptor != null) {
                            json = interceptor.interceptor(response, json);
                        }
                    } else {
                        if (interceptor != null) {
                            json = interceptor.interceptor(response, testData);
                        }
                    }
                    if (!String.class.equals(cla)) {
                        T gson = JPathGson.fromJson(json, jPath, cla);
                        upFileResult.result = gson;
                        subscriber.onNext(upFileResult);
                    } else {
                        T gson = JsonPath.parse(json).read(jPath);
                        upFileResult.result = gson;
                        subscriber.onNext(upFileResult);
                    }

                    subscriber.onComplete();

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Flowable<UpFileResult<String>> requestFile(final AsHttpInterceptor interceptor, final String jPath, final String testData) {
        return Flowable.create(new FlowableOnSubscribe<UpFileResult<String>>() {

            @Override
            public void subscribe(FlowableEmitter<UpFileResult<String>> subscribe) throws Exception {
                AsHttp.this.subscriber = subscribe;
                Call call = buildOkHttpCall();
                try {
                    String json = null;
                    Response response = null;
                    if (testData == null) {
                        Response rs = call.execute();
                        response = rs.networkResponse();
                        json = rs.body().string();
                        if (interceptor != null) {
                            json = interceptor.interceptor(response, json);
                        }
                    } else {
                        if (interceptor != null) {
                            json = interceptor.interceptor(response, testData);
                        }
                        L.json(json);
                    }
                    upFileResult.result = json;


                    subscriber.onNext(upFileResult);
                    subscriber.onComplete();

                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        }, BackpressureStrategy.DROP)
//               .sample(150, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }


    public <T> Observable<T> request(final AsHttpInterceptor interceptor, final java.lang.reflect.Type cla, final String jPath, final String testData) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> subscriber) throws Exception {
                try {
                    Call call = buildOkHttpCall();

                    String json = null;
                    Response response = null;
                    if (testData == null) {
                        Response rs = call.execute();
                        response = rs.networkResponse();
                        json = rs.body().string();
                        L.d(json);
                    } else {
                        json = testData;
                    }
                    if (interceptor != null) {
                      json = interceptor.interceptor(response, json);
                    }

                    if (!String.class.equals(cla)) {
                            T gson = JPathGson.fromJson(json, jPath, cla);
                            subscriber.onNext(gson);
                    } else {

                        String nodeString = JPathGson.readJsonPath(json, jPath);
                        subscriber.onNext((T) nodeString);
                    }
                    subscriber.onComplete();

                } catch (Exception e) {
                    try {
                        if (interceptor != null) {
                            subscriber.onError(interceptor.handlerThrowable(e));
                        } else {
                            subscriber.onError(e);
                        }
                    } catch (Exception ex) {
                        subscriber.onError(ex);
                    }
                }
//                if(ex!=null)subscriber.onError(ex);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }


    public Flowable<String> request(final AsHttpInterceptor interceptor) {
        L.d("call:request");
        return Flowable.create(new FlowableOnSubscribe<String>() {

            @Override
            public void subscribe(FlowableEmitter<String> subscriber) throws Exception {
                try {
//                    AsHttp.this.subscriber=subscriber;
                    Call call = buildOkHttpCall();
                    L.d("call:start");
                    Response rs = call.execute();
                    Response response = rs.networkResponse();
                    String json = rs.body().string();
                    L.d(json);
                    if (interceptor != null) {
                        json = interceptor.interceptor(rs.networkResponse(), json);
                    }
                    L.d("call:" + json);
                    subscriber.onNext(json);
//                    if(rs!=null)rs.close();
                    subscriber.onComplete();

                    L.d("call:onComplete");
                } catch (Exception e) {
                    L.d(e);
                    subscriber.onError(e);
                    if (interceptor != null) {
                        interceptor.handlerThrowable(e);
                    }
                }
            }
        }, BackpressureStrategy.DROP)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Flowable<Integer> downloadFileRx(File file) {
        return downloadFileRx(file,buildOkHttpCall());
    }

    public Flowable<Integer> downloadFileRx(final File file, final Call call) {
        return Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final FlowableEmitter<Integer> subscriber) throws Exception {
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        D.show(""+e.toString());
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                Log.d("h_bl", "progress=" + progress);
                                subscriber.onNext(progress);
                            }
                            fos.flush();
                            subscriber.onComplete();
                            Log.d("h_bl", "文件下载成功");
                        } catch (Exception e) {
                            Log.d("h_bl", "文件下载失败");
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                            }

                        }
                    }
                });

            }

        }, BackpressureStrategy.DROP)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }


    public Method getMethod() {
        return method;
    }

    public AsHttp setMethod(Method method) {
        this.method = method;
        return this;
    }

    public String getjPath() {
        return jPath;
    }

    public AsHttp setjPath(String jPath) {
        this.jPath = jPath;
        return this;
    }


    public AsHttp url(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }




    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the {@code object} whose type arguments are needed.
     * @return an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * @see {@link Class#getGenericSuperclass()}
     * @see {@link ParameterizedType#getActualTypeArguments()}
     */
    private static java.lang.reflect.Type[] getParameterizedTypes(Object object) {
        java.lang.reflect.Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }


    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *
     *@param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     *         determined
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        java.lang.reflect.Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        java.lang.reflect.Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }


    public Interceptor getOkHttpInterceptor() {
        return okHttpInterceptor;
    }

    public AsHttp setOkHttpInterceptor(Interceptor okHttpInterceptor) {
        this.okHttpInterceptor = okHttpInterceptor;
        return this;
    }

    public AsHttpInterceptor getAsHttpInterceptor() {
        return asHttpInterceptor;
    }

    public AsHttp setAsHttpInterceptor(AsHttpInterceptor asHttpInterceptor) {
        this.asHttpInterceptor = asHttpInterceptor;
        return this;
    }

    public AsHttpFactory getAsHttpfactory() {
        return mAsHttpfactory;
    }

    public AsHttp setAsHttpfactory(AsHttpFactory mAsHttpfactory) {
        this.mAsHttpfactory = mAsHttpfactory;
        return this;
    }
}
