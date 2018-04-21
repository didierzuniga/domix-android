package co.domix.android.domiciliary.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.domiciliary.presenter.DomiciliaryScorePresenter;
import co.domix.android.domiciliary.presenter.DomiciliaryScorePresenterImpl;

/**
 * Created by unicorn on 11/13/2017.
 */

public class DomiciliaryScore extends AppCompatActivity implements DomiciliaryScoreView {

    private RatingBar ratingBarDomiToUser;
    private ProgressBar progressBar;
    private Button buttonSendScoreDomiToUser;
    private DomixApplication app;
    private DomiciliaryScorePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domiciliary_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_qualify_user));

        app = (DomixApplication) getApplicationContext();
        presenter = new DomiciliaryScorePresenterImpl(this);
        progressBar = (ProgressBar) findViewById(R.id.prgBarRateDomi);
        ratingBarDomiToUser = (RatingBar) findViewById(R.id.rateDomiToUser);
        buttonSendScoreDomiToUser = (Button) findViewById(R.id.buttonSendScoreDomiToUser);
        buttonSendScoreDomiToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                Double score = Double.valueOf(ratingBarDomiToUser.getRating());
                sendScore(score);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getResources().getString(R.string.toast_action_not_allowed), Toast.LENGTH_SHORT).show();
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
    public void sendScore(Double score) {
        presenter.sendScore(score, app.idOrder, this);
    }

    @Override
    public void responseBackDomiciliaryActivity() {
        hideProgressBar();
        app.idOrder = 0;
        Intent intent = new Intent(this, Domiciliary.class);
        startActivity(intent);
        super.finish();
    }
}
