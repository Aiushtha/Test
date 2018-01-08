package org.lxz.utils.http;

import java.io.IOException;

/**
 * Created by Lin on 2017/4/21.
 */

public class HttpException extends IOException {
    private int code;
    private String msg;

    public HttpException(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public HttpException(Throwable e) {
        code=-1;
        msg=e.getMessage();
    }

    public int getCode() {
        return code;
    }

    public HttpException setCode(int code) {
        this.code = code;
        return this;
    }


    @Override
    public String getMessage() {
        return msg;
    }


    @Override
    public String toString() {
        return  msg ;
    }
}