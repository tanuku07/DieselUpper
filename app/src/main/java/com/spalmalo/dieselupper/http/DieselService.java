package com.spalmalo.dieselupper.http;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
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
        @POST("/index.php")
        Observable<Response> login(@Query("act") String act, @Query("CODE") String code, @Body Map<String,String> body);
    }

    public Observable<Response> login(){
        Map<String, String> body=new HashMap<>();
        body.put("UserName", mUser);
        body.put("PassWord", mPassword);
        body.put("Submit", "Войти");
        return mService.login("Login","01",body);
    }


}
