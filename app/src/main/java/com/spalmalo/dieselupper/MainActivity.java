package com.spalmalo.dieselupper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.spalmalo.dieselupper.http.DieselService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import retrofit.client.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public String mUser = "kgking";
    public String mPass = "syncmaster793df";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new DieselService(mUser, mPass, "test")
                .login()
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
                        Log.i("Result ", result);
                        File file = new File(getExternalCacheDir(), "result.txt");
                        PrintWriter out = null;
                        try {
                            out = new PrintWriter(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        out.print(result);
                        if (result.toLowerCase().contains(mUser.toLowerCase()))
                            Toast.makeText(getApplicationContext(), "Authorized", Toast.LENGTH_LONG).show();
                        else if (result.contains("<div id=\"userlinksguest\">"))
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
