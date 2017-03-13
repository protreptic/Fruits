package com.example.mobdev_3.fruits.service.fruits.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author Peter Bukhal (peter.bukhal@gmail.com)
 */
public final class RequestInterceptor implements Interceptor {

    private final int version;

    public RequestInterceptor(int version) {
        this.version = version;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(
                chain.request()
                        .newBuilder()
                        .addHeader("Accept", "application/vnd.docplus.doctor.v" + version + "+json")
                        .addHeader("Accept-Charset", "utf-8")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        //.addHeader("Authorization", "Bearer -")
                        .build());
    }

}
