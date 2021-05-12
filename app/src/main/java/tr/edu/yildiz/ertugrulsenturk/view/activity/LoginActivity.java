package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.UserDataBase;

public class LoginActivity extends AppCompatActivity {
    private static Handler handler;
    private int remainingTrial;
    private TextView password, eMail;
    private int second;
    private Runnable timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        remainingTrial = 3;
        second = 0;
        password = findViewById(R.id.userLoginPassword);
        eMail = findViewById(R.id.userLoginEmail);
        tryLogin();
    }

    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

        startActivity(intent);
    }

    public void signIn(View view) {
        if (second == 0) {
            User user = UserDataBase.getUser(this, MODE_PRIVATE, eMail.getText().toString(), password.getText().toString());
            if (remainingTrial > 0 && user == null) {
                Toast.makeText(this, getResources().getQuantityString(R.plurals.login_failed_remaining_count, remainingTrial, remainingTrial), Toast.LENGTH_SHORT).show();
                remainingTrial--;
            } else if (remainingTrial == 0) {
                Toast.makeText(this, getString(R.string.login_blocked_first_time), Toast.LENGTH_SHORT).show();
                startTimer();
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            } else if (user != null) {
                saveLogin(user);
                ActivityCompat.finishAffinity(this);
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.putExtra("user", user);
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getQuantityString(R.plurals.login_blocked_with_remaining_time, second, second), Toast.LENGTH_SHORT).show();
        }
    }

    private void tryLogin() {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        String mail = preferences.getString("email", "");
        String pass = preferences.getString("password", "");
        User user = UserDataBase.getUser(this, MODE_PRIVATE, mail, pass);
        if (user != null) {
            ActivityCompat.finishAffinity(this);
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

    private void saveLogin(User user) {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", user.getEMail());
        editor.putString("password", user.getPassword());
        editor.apply();
    }

    private void startTimer() {
        second = 60;
        handler = new Handler(Looper.myLooper());
        // Runnable for timer activates with handler
        timer = () -> {
            second--;
            handler.postDelayed(timer, 1000);
            if (second == 0) {
                remainingTrial = 3;
                handler.removeCallbacks(timer);
            }
        };
        handler.post(timer);
    }

}