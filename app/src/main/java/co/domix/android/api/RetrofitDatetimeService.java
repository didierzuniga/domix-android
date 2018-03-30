package co.domix.android.api;

import co.domix.android.model.Time;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by unicorn on 3/29/2018.
 */

public interface RetrofitDatetimeService {

    @GET("datetime/{code}")
//    Call<Time> loadTime();
    Call<Time> loadTime(@Path("code") String code);
}
