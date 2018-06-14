package co.domix.android.customizer.view;

import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.EditProfilePresenter;
import co.domix.android.customizer.presenter.EditProfilePresenterImpl;

public class EditProfile extends AppCompatActivity implements EditProfileView {

    private TextInputEditText data;
    private TextView txtFirstName, txtLastName, txtCellphone, txtDni;
    private ProgressBar progressBar;
    private Button btnSave;
    private EditProfilePresenter presenter;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter = new EditProfilePresenterImpl(this);
        app = (DomixApplication) getApplicationContext();

        progressBar = (ProgressBar) findViewById(R.id.progressBarEdit);
        data = (TextInputEditText) findViewById(R.id.editFirst);
        txtFirstName = (TextView) findViewById(R.id.idTextFirstName);
        txtLastName = (TextView) findViewById(R.id.idTextLastName);
        txtCellphone = (TextView) findViewById(R.id.idTextCellphone);
        txtDni = (TextView) findViewById(R.id.idTextDni);
        btnSave = (Button) findViewById(R.id.btnSaveData);

        if (getIntent().getIntExtra("field", 0) == 1){
            txtFirstName.setVisibility(View.VISIBLE);
            data.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            data.setHint(getIntent().getStringExtra("data"));
        } else if (getIntent().getIntExtra("field", 0) == 2){
            txtLastName.setVisibility(View.VISIBLE);
            data.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            data.setHint(getIntent().getStringExtra("data"));
        } else if (getIntent().getIntExtra("field", 0) == 3){
            txtCellphone.setVisibility(View.VISIBLE);
            data.setInputType(InputType.TYPE_CLASS_PHONE);
            data.setHint(getIntent().getStringExtra("data"));
        } else if (getIntent().getIntExtra("field", 0) == 4){
            txtDni.setVisibility(View.VISIBLE);
            data.setHint(getIntent().getStringExtra("data"));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                presenter.changePersonalData(app.uId,
                                             getIntent().getIntExtra("field", 0),
                                             data.getText().toString());
            }
        });
    }

    @Override
    public void dataChangeSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        onBackPressed();
                    }
                }, 2000);}});
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
