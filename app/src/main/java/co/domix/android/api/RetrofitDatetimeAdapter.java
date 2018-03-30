package co.domix.android.api;

import android.os.Build;

import co.domix.android.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by unicorn on 3/29/2018.
 */

public class RetrofitDatetimeAdapter {
    Retrofit retrofit;
    public RetrofitDatetimeAdapter(){
    }

    public Retrofit getAdapter(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_DATETIME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
