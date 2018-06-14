package co.domix.android.customizer.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.customizer.presenter.ProfilePresenterImpl;
import co.domix.android.home.view.Home;
import co.domix.android.login.view.Login;
import co.domix.android.utils.ToastsKt;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements ProfileView, NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBarProfile;
    private CircleImageView ivProfile;
    private LinearLayout editFirstName, editLastName, editCellphone, editDni;
    private TextView firstname, lastname, cellphone, dni, email, myCredit, rateAsDomi, rateAsUser;
    private StorageReference storageReference;
    private Button btnUploadPhoto, btnChoosePhoto;
    private DomixApplication app;
    private ProfilePresenter presenter;
    private boolean verifyGlid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_profile);

        presenter = new ProfilePresenterImpl(this);
        app = (DomixApplication) getApplicationContext();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBarProfile = (ProgressBar) findViewById(R.id.progressBarProfile);
        editFirstName = (LinearLayout) findViewById(R.id.idEditFirstName);
        editLastName = (LinearLayout) findViewById(R.id.idEditLastName);
        editCellphone = (LinearLayout) findViewById(R.id.idEditCellphone);
        editDni = (LinearLayout) findViewById(R.id.idEditDni);

        firstname = (TextView) findViewById(R.id.idFirstnameProfile);
        lastname = (TextView) findViewById(R.id.idLastnameProfile);
        cellphone = (TextView) findViewById(R.id.idCellphoneProfile);
        dni = (TextView) findViewById(R.id.idDniProfile);
        email = (TextView) findViewById(R.id.idEmailProfile);
        myCredit = (TextView) findViewById(R.id.idMyCredit);
        rateAsDomi = (TextView) findViewById(R.id.idRateAsDomi);
        rateAsUser = (TextView) findViewById(R.id.idRateAsUser);
        showProgressBar();

        ivProfile = (CircleImageView) findViewById(R.id.imageProfile);
        btnChoosePhoto = (Button) findViewById(R.id.choosePhoto);
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, ""), 1);
                verifyGlid = false;
            }
        });

        btnUploadPhoto = (Button) findViewById(R.id.buttonRefresh);
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                StorageReference refImageProfile = storageReference.child("image_profile/" + app.uId + "/img1.png");
                ivProfile.setDrawingCacheEnabled(true);
                ivProfile.buildDrawingCache();

                Bitmap bitmap = ivProfile.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                byte[] imgProfile = baos.toByteArray();
                UploadTask uploadTask = refImageProfile.putBytes(imgProfile);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hideProgressBar();
                        putTrueImage();
                        Toast.makeText(Profile.this, getResources().getString(R.string.toast_success_upload), Toast.LENGTH_SHORT).show();
//                        btnChoosePhoto.setVisibility(View.VISIBLE);
                        btnUploadPhoto.setVisibility(View.GONE);
                    }
                });
            }
        });

        editFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("field", 1); // To change firstname
                intent.putExtra("data", firstname.getText().toString());
                startActivity(intent);
            }
        });

        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("field", 2); // To change lastname
                intent.putExtra("data", lastname.getText().toString());
                startActivity(intent);
            }
        });

        editCellphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("field", 3); // To change cellphone
                intent.putExtra("data", cellphone.getText().toString());
                startActivity(intent);
            }
        });

        editDni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.putExtra("field", 4); // To change dni
                intent.putExtra("data", dni.getText().toString());
                startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0){
            if (requestCode == 1){
                Uri imageUri = data.getData();
                if (imageUri != null){
                    ivProfile.setImageURI(imageUri);
                    btnUploadPhoto.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstName, String lastName, String dnidentification,
                                 String phone, String mail, String scoreAsDomi, String scoreAsUser, String credit) {
        if (firstName != null){
            firstname.setText(firstName);
        }
        if (lastName != null){
            lastname.setText(lastName);
        }
        if (phone != null){
            cellphone.setText(phone);
        }
        if (dnidentification != null){
            dni.setText(dnidentification);
        }
        email.setText(mail);
        myCredit.setText(credit);
        rateAsDomi.setText(scoreAsDomi);
        rateAsUser.setText(scoreAsUser);
        verifyGlid = verifyGlide;
        if (verifyGlid) {
            executeGlide();
        } else {
            hideProgressBar();
            ivProfile.setImageResource(R.drawable.ic_add_photo);
        }
    }

    @Override
    public void queryVerifyGlide() {
        presenter.queryImageSeted(app.uId);
    }

    @Override
    public void putTrueImage() {
        presenter.trueImageSeted(app.uId);
        onStart();
    }

    @Override
    public void executeGlide() {
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(storageReference.child("image_profile/" + app.uId + "/img1.png"))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(500, 500)
                .centerCrop()
                .into(ivProfile);
        hideProgressBar();
    }

    @Override
    public void showProgressBar() {
        progressBarProfile.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBarProfile.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryVerifyGlide();
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
        finish();
    }

    @Override
    public void goHistory() {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goSetting() {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void goPayment() {
        Intent intent = new Intent(this, PaymentMethod.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void logOut() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
