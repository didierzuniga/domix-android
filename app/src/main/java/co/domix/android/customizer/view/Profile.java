package co.domix.android.customizer.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.ProfilePresenter;
import co.domix.android.customizer.presenter.ProfilePresenterImpl;
import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements ProfileView {

    private ProgressBar progressBarProfile;
    private ImageView ivProfile;
    private TextView firstname, lastname, email, rateAsDomi, rateAsUser;
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
        firstname = (TextView) findViewById(R.id.idFirstnameProfile);
        email = (TextView) findViewById(R.id.idEmailProfile);
        lastname = (TextView) findViewById(R.id.idLastnameProfile);
        rateAsDomi = (TextView) findViewById(R.id.idRateAsDomi);
        rateAsUser = (TextView) findViewById(R.id.idRateAsUser);
        showProgressBar();
        queryVerifyGlide();

        ivProfile = (ImageView) findViewById(R.id.imageProfile);
        btnChoosePhoto = (Button) findViewById(R.id.choosePhoto);
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,""), 1);
                verifyGlid = false;
            }
        });

        btnUploadPhoto = (Button) findViewById(R.id.buttonRefresh);
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                StorageReference refImageProfile = storageReference.child("image_profile/"+app.uId+"/img1.png");
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
                        btnChoosePhoto.setVisibility(View.VISIBLE);
                        btnUploadPhoto.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            Uri imageUri = data.getData();
            if (imageUri != null){
                ivProfile.setImageURI(imageUri);
                btnChoosePhoto.setVisibility(View.GONE);
                btnUploadPhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void responseDataUser(boolean verifyGlide, String firstName, String lastName, String mail, String scoreAsDomi,
                                 String scoreAsUser) {
        firstname.setText(firstName);
        lastname.setText(lastName);
        email.setText(mail);
        rateAsDomi.append(" " + scoreAsDomi);
        rateAsUser.append(" " + scoreAsUser);
        verifyGlid = verifyGlide;
        if (verifyGlid){
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
                .skipMemoryCache(false)
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
}
