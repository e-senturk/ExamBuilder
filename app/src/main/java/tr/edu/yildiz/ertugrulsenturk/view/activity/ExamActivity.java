package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.Intent;
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
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.tools.ExamTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.QuestionTools;
import tr.edu.yildiz.ertugrulsenturk.view.fragment.ExamFragment;
import tr.edu.yildiz.ertugrulsenturk.view.fragment.ResultFragment;

public class ExamActivity extends AppCompatActivity {
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


        TextView userNameText = findViewById(R.id.examUserName);
        TextView secondText = findViewById(R.id.examSecond);

        Intent intent = getIntent();
        Exam exam = (Exam) intent.getSerializableExtra("exam");
        currentUser = (User) intent.getSerializableExtra("user");


        questions = ExamTools.convertExamToQuestions(this, MODE_PRIVATE, exam, currentUser);
        String examCode = exam.getExamCode();
        setTitle(examCode);
        examTime = exam.getExamTime() * 60;


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
        String examText = userNameText.getText() + "\n" + currentUser.getUserFirstName() + " " + currentUser.getUserLastName();
        userNameText.setText(examText);
        startExamFragment();
    }

    private void startExamFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        examFragment = new ExamFragment(questions);
        fragmentTransaction.add(R.id.examFragmentLayout, examFragment).commit();
    }

    private void startResultFragment() {
        int[] results = QuestionTools.calculateResults(ExamRecyclerViewAdapter.getSelectedChoices(), questions);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(examFragment);
        ResultFragment resultFragment = new ResultFragment(currentUser.getUserFirstName(), results[0], results[1], results[2]);
        fragmentTransaction.add(R.id.examFragmentLayout, resultFragment).commit();
    }

    public void finishExamButton(View view) {
        if(examTime >0)
            examTime = 0;
        else
            finish();
    }
}