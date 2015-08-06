package com.spalmalo.dieselupper.http;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by kani on 8/5/15.
 */
public class DieselService {
    private String mUser;
    private String mPassword;
    private String mPost;
    private WebService mService;

    public DieselService(String mUser, String mPassword, String mPost) {
        this.mUser = mUser;
        this.mPassword = mPassword;
        this.mPost = mPost;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://diesel.elcat.kg")
                .build();
        mService = restAdapter.create(WebService.class);
    }

    private interface WebService {
        @FormUrlEncoded
        @POST("/index.php?act=Login&CODE=01")
        Observable<Response> login(@Field("UserName") String user, @Field("PassWord") String password);
    }

    public Observable<Response> login() {
        return mService.login(mUser, mPassword);
    }


}
