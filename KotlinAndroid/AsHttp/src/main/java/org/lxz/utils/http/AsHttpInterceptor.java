package org.lxz.utils.http;

import okhttp3.Response;

/**
 * Created by Lin on 2017/4/10.
 */
public interface AsHttpInterceptor {
    public String interceptor(Response response, String json) throws Exception;
    public HttpException handlerThrowable(Throwable e);
    public String httpCodeMessage(int code);
}
