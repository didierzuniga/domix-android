package co.domix.android.login.interactor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import co.domix.android.R;
import co.domix.android.login.presenter.SplashPresenter;
import co.domix.android.login.repository.SplashRepository;
import co.domix.android.login.repository.SplashRepositoryImpl;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by unicorn on 11/22/2017.
 */

public class SplashInteractorImpl implements SplashInteractor {

    private LocationManager locationManager;
    private SplashPresenter presenter;
    private SharedPreferences location;
    private SplashRepository repository;

    public SplashInteractorImpl(SplashPresenter presenter) {
        this.presenter = presenter;
        repository = new SplashRepositoryImpl(presenter);
    }

    @Override
    public void queryStatePosition(String uid, Activity activity) {
        repository.queryStatePosition(uid, activity);
        location = activity.getSharedPreferences(activity.getString(R.string.const_sharedpreference_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = location.edit();
        editor.putString("latFrom", "");
        editor.putString("lonFrom", "");
        editor.putString("latTo", "");
        editor.putString("lonTo", "");
        editor.commit();
    }

    @Override
    public void verifyNetworkAndInternet(Activity activity, boolean isOnline, FirebaseUser firebaseUser, String uid) {
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!network_enabled) {
            presenter.executeAlertNoNetwork();
        } else {
            if (!isOnline){
                presenter.notInternetConnection();
            } else {
                if (firebaseUser != null) {
                    repository.queryStatePosition(uid, activity);
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    presenter.goLogin();
                                }
                            }, 1000);
                        }
                    });
                }
            }
        }
    }
}
