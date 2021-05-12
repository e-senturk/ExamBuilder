package tr.edu.yildiz.ertugrulsenturk.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.adapter.ExamRecyclerViewAdapter;
import tr.edu.yildiz.ertugrulsenturk.model.Question;

public class ExamFragment extends Fragment {
    private final ArrayList<Question> questions;

    public ExamFragment(ArrayList<Question> questions) {
        this.questions = questions;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_exam, container, false);
        RecyclerView recyclerView = viewGroup.findViewById(R.id.examRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        ExamRecyclerViewAdapter examRecyclerViewAdapter = new ExamRecyclerViewAdapter(questions);
        recyclerView.setAdapter(examRecyclerViewAdapter);
        return viewGroup;
    }
}
