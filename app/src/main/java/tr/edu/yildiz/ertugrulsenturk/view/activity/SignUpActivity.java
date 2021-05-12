package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import tr.edu.yildiz.ertugrulsenturk.service.database.UserDataBase;
import tr.edu.yildiz.ertugrulsenturk.service.tools.BlobTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.MediaTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.UserValidationTools;

public class SignUpActivity extends AppCompatActivity {
    private Bitmap userBitmap;
    private ImageView userImage;
    private TextView userSignUpName;
    private TextView userSignUpSurname;
    private TextView userPhoneNumber;
    private TextView userEmail;
    private TextView userSignUpPassword;
    private TextView userSignUpPasswordRe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // starting activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle(R.string.sign_up);

        // attaching to xml elements
        userImage = findViewById(R.id.userImage);
        userSignUpName = findViewById(R.id.userSignUpName);
        userSignUpSurname = findViewById(R.id.userSignUpSurname);
        userPhoneNumber = findViewById(R.id.userSignUpPhoneNumber);
        userEmail = findViewById(R.id.userSignUpEMail);
        userSignUpPassword = findViewById(R.id.userSignUpPassword);
        userSignUpPasswordRe = findViewById(R.id.userSignUpPasswordRe);
        userBitmap = null;
        // Styles edit text for phone number input
        userPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    public void signUp(View view) {
        try {
            String firstName = UserValidationTools.validateUserFirstName(this, userSignUpName.getText().toString());
            String lastName = UserValidationTools.validateUserLastName(this, userSignUpSurname.getText().toString());
            String phoneNumber = UserValidationTools.validatePhoneNumber(this, userPhoneNumber.getText().toString());
            String email = UserValidationTools.validateUserEmail(this, userEmail.getText().toString());
            String password = UserValidationTools.validatePassword(this, userSignUpPassword.getText().toString());
            UserValidationTools.validatePasswordRe(this, userSignUpPassword.getText().toString(), userSignUpPasswordRe.getText().toString());
            if (UserDataBase.isExistingUser(this, MODE_PRIVATE, firstName, lastName, email)) {
                throw new InvalidObjectException(getString(R.string.user_already_exists));
            }
            User user = new User(firstName, lastName, phoneNumber, email, password, BlobTools.getBytesFromImage(userBitmap));
            UserDataBase.addUser(this, MODE_PRIVATE, user);
            saveLogin(user);
            ActivityCompat.finishAffinity(this);
            Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }

    public void selectUserImage(View view) {
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
            userBitmap= MediaTools.initializeImageView(this, data.getData(), userImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveLogin(User user) {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", user.getEMail());
        editor.putString("password", user.getPassword());
        editor.apply();
    }

}