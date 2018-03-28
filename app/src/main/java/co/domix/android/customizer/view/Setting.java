package co.domix.android.customizer.view;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import co.domix.android.R;

public class Setting extends AppCompatActivity {

    private Switch aSwitch;
    private SharedPreferences location;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_setting);

        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();

        aSwitch = (Switch) findViewById(R.id.switchNotifications);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("notifications", true);
                    editor.commit();
                } else {
                    editor.putBoolean("notifications", false);
                    editor.commit();
                }
            }
        });
    }
}
