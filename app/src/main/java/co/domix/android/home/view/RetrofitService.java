package co.domix.android.home.view;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by unicorn on 3/29/2018.
 */

public interface RetrofitService {
    @GET("datetime/CO")
    Call<TimeFromWeb> loadTime();
}
