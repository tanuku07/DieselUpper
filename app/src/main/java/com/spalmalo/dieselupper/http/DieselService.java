package com.spalmalo.dieselupper.http;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by kani on 8/5/15.
 */
public class DieselService {
    private String mUser;
    private String mPassword;
    private String mPostId;
    private WebService mService;
    private String mCookies;

    public DieselService(String user, String password, String cookies, String postId) {
        this.mUser = user;
        this.mCookies = cookies;
        this.mPassword = password;
        this.mPostId = postId;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://diesel.elcat.kg")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Cookie", mCookies);
                    }
                })
                .build();
        mService = restAdapter.create(WebService.class);
    }

    private interface WebService {
        @FormUrlEncoded
        @POST("/index.php?act=Login&CODE=01")
        Observable<Response> login(@Field("UserName") String user, @Field("PassWord") String password);

        @GET("/index.php")
        Observable<Response> getPost(@Query("showtopic") String id);

        @FormUrlEncoded
        @POST("/index.php")
        Observable<Response> newUp(@Field("act") String act,
                                   @Field("CODE") String code,
                                   @Field("f") String f,
                                   @Field("t") String t,
                                   @Field("st") String st,
                                   @Field("auth_key") String authKey,
                                   @Field("fast_reply_used") String fastReplyUsed,
                                   @Field("Post") String post);
    }

    public Observable<Response> login() {
        return mService.login(mUser, mPassword);
    }

    public Observable<Response> getPost() {
        return mService.getPost(mPostId);
    }

    public Observable<Response> newUp(String f, String authKey) {
        return mService.newUp("Post", "03", f, getPostId(), "99999", authKey, "1", "test");
    }

    public String getPostId() {
        return mPostId;
    }

}
