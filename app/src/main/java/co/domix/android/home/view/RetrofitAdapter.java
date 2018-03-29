package co.domix.android.home.view;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by unicorn on 3/29/2018.
 */

public class RetrofitAdapter {
    Retrofit retrofit;
    public RetrofitAdapter(){
    }

    public Retrofit getAdapter(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://domix-test.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
