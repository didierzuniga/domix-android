package co.domix.android.customizer.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.TotalToPayPresenter;
import co.domix.android.customizer.presenter.TotalToPayPresenterImpl;
import co.domix.android.home.view.Home;
import co.domix.android.login.view.Login;
import co.domix.android.utils.ToastsKt;

public class AmountToPay extends AppCompatActivity implements AmountToPayView, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBarPay;
    private LinearLayout linearPayPerDomicilies, linearPaytaxes, linearPayTotal;
    private TextView toPayDomix, toPayDomixTotal, toPayTaxe;
    private Button buttonToPay;
    private String miniPayment;
    private int balanceUpdate;
    private List<String> list;
    private boolean enablePayment;
    private DomixApplication app;
    private TotalToPayPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_to_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.text_make_payment);
        presenter = new TotalToPayPresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        progressBarPay = (ProgressBar) findViewById(R.id.progressBarPay);
        showProgressBar();

        linearPayPerDomicilies = (LinearLayout) findViewById(R.id.linearPayPerDomicilies);
        linearPaytaxes = (LinearLayout) findViewById(R.id.linearPayTaxes);
        linearPayTotal = (LinearLayout) findViewById(R.id.linearPayTotal);
        linearPayPerDomicilies.setVisibility(View.GONE);
        linearPaytaxes.setVisibility(View.GONE);
        linearPayTotal.setVisibility(View.GONE);
        toPayDomix = (TextView) findViewById(R.id.toPayDomix);
        toPayDomixTotal = (TextView) findViewById(R.id.toPayDomixTotal);
        toPayTaxe = (TextView) findViewById(R.id.toPayTaxe);
        buttonToPay = (Button) findViewById(R.id.btnGoToPay);
        buttonToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enablePayment){
                    ToastsKt.toastShort(AmountToPay.this, "Pagando...");
                    presenter.goPayU(list, balanceUpdate);
                } else {
                    ToastsKt.toastShort(AmountToPay.this, getString(R.string.text_minimum_amount) + " " + miniPayment);
                }
            }
        });
        buttonToPay.setEnabled(false);
        presenter.queryOrderToPay(app.uId, app.payMethod);
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
    public void showProgressBar() {
        progressBarPay.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarPay.setVisibility(View.GONE);
    }

    @Override
    public void responseTotalToPayCash(String commissionDomix, String payTaxe, String payTotalToDomix,
                                       String minPayment, boolean enableButtonPay, int balanceToUpdate,
                                       List<String> listOrders, String currencyCode) {
        list = listOrders;
        balanceUpdate = balanceToUpdate;
        buttonToPay.setEnabled(true);
        enablePayment = true;
        miniPayment = minPayment + " " + currencyCode;
        if (!enableButtonPay){
            enablePayment = false;
            ToastsKt.toastLong(AmountToPay.this, getString(R.string.text_minimum_amount) + " " + miniPayment);
        }
        hideProgressBar();
        linearPayPerDomicilies.setVisibility(View.VISIBLE);
        linearPaytaxes.setVisibility(View.VISIBLE);
        linearPayTotal.setVisibility(View.VISIBLE);
        toPayDomix.setText(commissionDomix + " " + currencyCode);
        toPayTaxe.setText(payTaxe + " " + currencyCode);
//        This data has ben send to PayU
//        float py = Float.valueOf(payTotalToDomix);
//        int yp = (int)(py);
        toPayDomixTotal.setText(payTotalToDomix + " " + currencyCode);
    }

    @Override
    public void thereAreNotOrders() {
        ToastsKt.toastLong(AmountToPay.this, "No tienes saldos pendientes");
        Intent intent = new Intent(AmountToPay.this, PaymentMethod.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            goHome();
        } else if (id == R.id.nav_profile) {
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
    public void goHome() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
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
        Intent intent = new Intent(this, PaymentMethod.class);
        startActivity(intent);
    }

    @Override
    public void logOut() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
