package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.User;

public class MenuActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle(R.string.app_name);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
    }

    public void addQuestion(View view) {
        Intent intent = new Intent(MenuActivity.this, AddQuestionActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void listQuestions(View view) {
        Intent intent = new Intent(MenuActivity.this, QuestionListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void createExam(View view) {
        Intent intent = new Intent(MenuActivity.this, ExamGeneratorActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void examSettings(View view) {
        Intent intent = new Intent(MenuActivity.this, ExamSettingsActivity.class);
        startActivity(intent);
    }

    public void userInformation(View view) {
        Intent intent = new Intent(MenuActivity.this, UserInfoActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void logOut(View view) {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", "");
        editor.putString("password", "");
        editor.apply();
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}