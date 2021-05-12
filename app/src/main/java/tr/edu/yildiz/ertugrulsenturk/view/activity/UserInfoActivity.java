package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.InvalidObjectException;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.tools.BlobTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.MediaTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.UserValidationTools;
import tr.edu.yildiz.ertugrulsenturk.service.database.UserDataBase;

public class UserInfoActivity extends AppCompatActivity {
    private TextView userFirstName;
    private TextView userLastName;
    private TextView userEmail;
    private TextView userPhoneNumber;
    private TextView userPassword;
    private Bitmap userBitmap;
    private ImageView imageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setTitle(R.string.user_info);
        userFirstName = findViewById(R.id.userFirstNameInfo);
        userLastName = findViewById(R.id.userLastNameInfo);
        userEmail = findViewById(R.id.userEmailInfo);
        userPhoneNumber = findViewById(R.id.userPhoneNumberInfo);
        userPassword = findViewById(R.id.userPasswordInfo);
        imageInfo = findViewById(R.id.userImageInfo);
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        if (user != null) {
            if (user.getUserImage() != null && user.getUserImage().length != 0) {
                userBitmap = BlobTools.getImageFromBytes(user.getUserImage());
                imageInfo.setImageBitmap(userBitmap);
            }
            userFirstName.setText(user.getUserFirstName());
            userLastName.setText(user.getUserLastName());
            userEmail.setText(user.getEMail());
            userPhoneNumber.setText(user.getPhoneNumber());
            userPassword.setText(user.getPassword());
        }
    }

    public void updateUser(View view) {
        try {
            String name = UserValidationTools.validateUserFirstName(this, userFirstName.getText().toString());
            String surname = UserValidationTools.validateUserLastName(this, userLastName.getText().toString());
            String phoneNumber = UserValidationTools.validatePhoneNumber(this, userPhoneNumber.getText().toString());
            String email = UserValidationTools.validateUserEmail(this, userEmail.getText().toString());
            String password = UserValidationTools.validatePassword(this, userPassword.getText().toString());
            User user = new User(name, surname, phoneNumber, email, password, BlobTools.getBytesFromImage(userBitmap));
            UserDataBase.updateUser(this, MODE_PRIVATE, user);
            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", user.getEMail());
            editor.putString("password", user.getPassword());
            editor.apply();
            finish();
            Intent intent = new Intent(UserInfoActivity.this, MenuActivity.class);
            intent.putExtra("user", user);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void updateUserImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            userBitmap = MediaTools.initializeImageView(this,data.getData(), imageInfo);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}