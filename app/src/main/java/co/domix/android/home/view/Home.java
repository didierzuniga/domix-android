package co.domix.android.home.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.view.History;
import co.domix.android.customizer.view.Pay;
import co.domix.android.customizer.view.Profile;
import co.domix.android.customizer.view.Setting;
import co.domix.android.domiciliary.view.Domiciliary;
import co.domix.android.home.presenter.HomePresenter;
import co.domix.android.home.presenter.HomePresenterImpl;
import co.domix.android.login.view.Login;
import co.domix.android.user.view.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeView {

    private FirebaseAuth firebaseAuth;
    private LinearLayout linearRoot, linearNotInternet;
    private ProgressBar progressBar;
    private AlertDialog alert = null;
    private Button buttonGoUser, buttonGoDomiciliary, buttonRefresh;
    private HomePresenter presenter;
    public DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_home));

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Shrikhand-Regular.ttf");
        presenter = new HomePresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progressBarHome);
        linearRoot = (LinearLayout) findViewById(R.id.rootLinearHome);
        linearNotInternet = (LinearLayout) findViewById(R.id.notInternetHome);
        buttonGoUser = (Button) findViewById(R.id.button_be_user);
        buttonGoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUser();
            }
        });

        buttonGoDomiciliary = (Button) findViewById(R.id.button_be_domiciliary);
        buttonGoDomiciliary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goDomiciliary();
            }
        });

//        Test call timestamp with Retrofit

        Retrofit retrofit = new RetrofitAdapter().getAdapter();
        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<TimeFromWeb> call;
        call = service.loadTime();
        call.enqueue(new Callback<TimeFromWeb>() {
            @Override
            public void onResponse(Call<TimeFromWeb> call, Response<TimeFromWeb> response) {
                Log.w("jjj", "-> "+response.body().getTimestamp());
                Log.w("jjj", "-> "+response.body().getDate());
                Log.w("jjj", "-> "+response.body().getTime());
            }

            @Override
            public void onFailure(Call<TimeFromWeb> call, Throwable t) {
                Log.w("jjj", "Err-> "+t.getCause());
            }
        });





//        Test call timestamp with Retrofit

        buttonGoUser.setTypeface(font);
        buttonGoDomiciliary.setTypeface(font);

        buttonRefresh = (Button) findViewById(R.id.buttonRefreshHome);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.verifyLocationAndInternet(Home.this);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryLight)); //Change menu hamburguer color
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            goProfile();
        } else if (id == R.id.nav_history) {
            goHistory();
        } else if (id == R.id.nav_setting) {
            goSetting();
        } else if (id == R.id.nav_payment) {
            goPayment();
        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void goUser() {
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }

    @Override
    public void goDomiciliary() {
        Intent intent = new Intent(this, Domiciliary.class);
        startActivity(intent);
    }

    @Override
    public void goProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    @Override
    public void goHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

    @Override
    public void goSetting() {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }

    @Override
    public void goPayment() {
        Intent intent = new Intent(this, Pay.class);
        startActivity(intent);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void alertNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_location_deactivate)
                .setCancelable(false)
                .setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.message_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.super.finish();
            }
        });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void showNotInternet() {
        linearRoot.setVisibility(View.GONE);
        linearNotInternet.setVisibility(View.VISIBLE);
    }

    @Override
    public void showYesInternet() {
        linearNotInternet.setVisibility(View.GONE);
        linearRoot.setVisibility(View.VISIBLE);
    }

    @Override
    public void logOut() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.verifyLocationAndInternet(this);
    }
}
