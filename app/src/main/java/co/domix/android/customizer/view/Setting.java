package co.domix.android.customizer.view;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import co.domix.android.R;
import co.domix.android.customizer.presenter.SettingPresenter;
import co.domix.android.customizer.presenter.SettingPresenterImpl;

public class Setting extends AppCompatActivity implements SettingView {

    private Switch aSwitch;
    private Button buttonChangePassword;
    private Button buttonDeleteAccount;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences location;
    private SharedPreferences.Editor editor;
    private SettingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_setting);

        presenter = new SettingPresenterImpl(this);
        location = getSharedPreferences("domx_prefs", MODE_PRIVATE);
        editor = location.edit();

        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonDeleteAccount = (Button) findViewById(R.id.buttonDeleteAccount);
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

    @Override
    public void deleteAccount() {
        user = firebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("jjj", "User account deleted.");
                        }
                    }
                });
    }
}
