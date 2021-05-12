package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.adapter.QuestionSelectorRecyclerViewAdapter;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.QuestionDataBase;

public class QuestionSelectActivity extends AppCompatActivity {
    private ArrayList<Question> questions;
    private ArrayList<String> selectedQuestions;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_select);
        setTitle(getString(R.string.select_question));
        Intent intent = getIntent();
        User currentUser = (User) intent.getSerializableExtra("user");
        selectedQuestions = (ArrayList<String>) intent.getSerializableExtra("selected_questions");
        questions = QuestionDataBase.getQuestions(this, MODE_PRIVATE, currentUser);
        System.out.println(questions.size());
        setupRecycler();
    }

    private void setupRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewSelect);
        recyclerView.setLayoutManager(new LinearLayoutManager(QuestionSelectActivity.this));
        QuestionSelectorRecyclerViewAdapter questionListRecyclerViewAdapter = new QuestionSelectorRecyclerViewAdapter(questions, selectedQuestions);
        recyclerView.setAdapter(questionListRecyclerViewAdapter);
    }

    public void selectQuestion(View view) {
        ArrayList<String> selected = QuestionSelectorRecyclerViewAdapter.getSelected();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("selected", selected);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}