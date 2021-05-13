package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.adapter.ExamRecyclerViewAdapter;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.tools.ExamTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.QuestionTools;
import tr.edu.yildiz.ertugrulsenturk.view.fragment.ExamFragment;
import tr.edu.yildiz.ertugrulsenturk.view.fragment.ResultFragment;

public class ExamActivity extends AppCompatActivity {
    /**
     * An activity for showing exam
     */
    private static Handler handler;
    private ArrayList<Question> questions;
    private Runnable clock;
    private int examTime;
    private ExamFragment examFragment;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        // orientation changes are not allowed in this activity for preserving data
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        TextView userNameText = findViewById(R.id.examUserName);
        TextView secondText = findViewById(R.id.examSecond);

        // exam information and current user information received from exam generator activity
        Intent intent = getIntent();
        Exam exam = (Exam) intent.getSerializableExtra("exam");
        currentUser = (User) intent.getSerializableExtra("user");

        // all questions in the exam generated from exam class with external function because
        // exam class only contains question id's
        questions = ExamTools.convertExamToQuestions(this, MODE_PRIVATE, exam, currentUser);
        String examCode = exam.getExamCode();
        setTitle(examCode);
        // exam time keeps remaining time of the exam as a second
        examTime = exam.getExamTime() * 60;

        // a clock is generated for exam and result fragment started when the time is up.
        handler = new Handler(Looper.myLooper());
        clock = () -> {
            String min = examTime / 60 < 10 ? "0" + examTime / 60 : String.valueOf(examTime / 60);
            String sec = examTime % 60 < 10 ? "0" + examTime % 60 : String.valueOf(examTime % 60);
            String timeTextString = min + ":" + sec;
            secondText.setText(timeTextString);
            examTime--;
            handler.postDelayed(clock, 1000);
            if (examTime < 0) {
                handler.removeCallbacks(clock);
                startResultFragment();
            }
        };
        handler.post(clock);
        String examText = userNameText.getText() + " " + currentUser.getUserFirstName() + " " + currentUser.getUserLastName();
        userNameText.setText(examText);
        // exam fragment initialized
        startExamFragment();
    }

    // initializer for exam fragment
    private void startExamFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        examFragment = ExamFragment.newInstance(questions);
        fragmentTransaction.add(R.id.examFragmentLayout, examFragment).commit();
    }

    // stops exam fragment and starts result fragment
    private void startResultFragment() {
        int[] results = QuestionTools.calculateResults(ExamRecyclerViewAdapter.getSelectedChoices(), questions);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(examFragment);
        ResultFragment resultFragment = ResultFragment.newInstance(currentUser.getUserFirstName(), results[0], results[1], results[2]);
        fragmentTransaction.add(R.id.examFragmentLayout, resultFragment).commit();
    }

    // if exam in progress finish button finishes exam otherwise it terminates activity
    public void finishExamButton(View view) {
        if (examTime > 0)
            examTime = 0;
        else
            finish();
    }
}