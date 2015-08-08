package com.spalmalo.dieselupper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.spalmalo.dieselupper.http.DieselService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.client.Header;
import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String COOKIE_PREF = "cookies";
    private static final String COOKIE = "cookie_key";
    public SharedPreferences mSharedPreferences;
    /*    public String mUser = "kgking";
        public String mPass = "syncmaster793df";*/
    public String mUser = "aza_kush";
    public String mPass = "qwepoi19";
    private DieselService diesel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences(COOKIE_PREF, MODE_PRIVATE);

        start(mUser, mPass, "141889691");
    }

    private void start(String user, String password, String postId) {
        String cookie = mSharedPreferences.getString(COOKIE, "");
        diesel = new DieselService(mUser, mPass, cookie, postId);
        login();
    }

    private void login() {

        diesel.login()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response response) {
                        String result = "";
                        try {
                            result = convertStreamToString(response.getBody().in());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (result.toLowerCase().contains(mUser.toLowerCase())) {
                            Toast.makeText(getApplicationContext(), "Authorized", Toast.LENGTH_LONG).show();
                            getAndSaveCookies(response);
                            getPost();
                        } else if (result.contains("<div id=\"userlinksguest\">"))
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();


                    }
                });
    }

    private void getPost() {
        diesel.getPost()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response response) {
                        String result = "";
                        try {
                            result = convertStreamToString(response.getBody().in());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ((TextView) findViewById(R.id.text)).setText(result);

                        Pattern p = Pattern.compile("name=\"f\" value=\"(.*)\"");
                        Matcher m = p.matcher(result);
                        String forum = "";
                        if (m.find()) forum = result.substring(m.start() + 16, m.end() - 1);

                        String authKey = "";
                        p = Pattern.compile("name=\"auth_key\" value=\"(.*)\"");
                        m = p.matcher(result);
                        if (m.find()) authKey = result.substring(m.start() + 23, m.end() - 1);

                        if (!TextUtils.isEmpty(forum) && !TextUtils.isEmpty(authKey)) {
                            newUp(forum, authKey);
                        } else start(mUser, mPass, diesel.getPostId());
                    }
                });
    }

    private void newUp(String f, String authKey) {
        diesel.newUp(f, authKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Response response) {
                    }
                });
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void getAndSaveCookies(Response response) {
        String cookies = "";
        for (Header header : response.getHeaders()) {
            if (header.getName().equals("Set-Cookie")) {
                cookies += header.getValue();
            }
        }
        mSharedPreferences.edit()
                .putString(COOKIE, cookies)
                .apply();
    }

}
