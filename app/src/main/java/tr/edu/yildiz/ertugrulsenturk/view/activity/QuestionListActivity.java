package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.adapter.QuestionListRecyclerViewAdapter;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.QuestionDataBase;

public class QuestionListActivity extends AppCompatActivity {
    private static RecyclerView recyclerView;
    private ArrayList<Question> questions;
    private User currentUser;

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        setTitle(R.string.questions);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");
        questions = QuestionDataBase.getQuestions(this, MODE_PRIVATE, currentUser);
        setupRecycler();
    }

    private void setupRecycler() {
        recyclerView = findViewById(R.id.recyclerViewQuestionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(QuestionListActivity.this));
        QuestionListRecyclerViewAdapter questionListRecyclerViewAdapter = new QuestionListRecyclerViewAdapter(questions, currentUser);
        recyclerView.setAdapter(questionListRecyclerViewAdapter);
    }


}