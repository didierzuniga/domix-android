package co.domix.android.customizer.view;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.domix.android.DomixApplication;
import co.domix.android.R;

public class EditProfile extends AppCompatActivity {

    private TextInputEditText data;
    private TextView txtFirstName, txtLastName;
    private Button btnSave;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = (DomixApplication) getApplicationContext();

        data = (TextInputEditText) findViewById(R.id.editFirst);
        txtFirstName = (TextView) findViewById(R.id.idTextFirstName);
        txtLastName = (TextView) findViewById(R.id.idTextLastName);
        btnSave = (Button) findViewById(R.id.btnSaveData);

        if (getIntent().getIntExtra("field", 0) == 1){
            txtFirstName.setVisibility(View.VISIBLE);
            data.setText(getIntent().getStringExtra("data"));
        } else if (getIntent().getIntExtra("field", 0) == 2){
            txtLastName.setVisibility(View.VISIBLE);
            data.setText(getIntent().getStringExtra("data"));
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send
                String id = app.uId;
                int field = getIntent().getIntExtra("field", 0);
                String dt = data.getText().toString();

            }
        });
    }
}
