package tr.edu.yildiz.ertugrulsenturk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Question;

public class QuestionSelectorRecyclerViewAdapter extends RecyclerView.Adapter<QuestionSelectorRecyclerViewAdapter.RowHolder> {
    /**
     * Recycler view adapter for listing questions in question select
     * activity it is used for select questions for a new exam
     **/

    // questions stores all questions
    // selected stores only selected questions id's;
    private static ArrayList<Question> questions;
    private static Set<String> selected;

    public QuestionSelectorRecyclerViewAdapter(ArrayList<Question> questions, ArrayList<String> selectedQuestion) {
        QuestionSelectorRecyclerViewAdapter.questions = questions;
        // exam might already has selected questions so they need to be selected first
        if (selectedQuestion != null)
            selected = new HashSet<>(selectedQuestion);
        else
            selected = new HashSet<>();
    }

    public static ArrayList<String> getSelected() {
        return new ArrayList<>(selected);
    }

    @NonNull
    @NotNull
    @Override
    public QuestionSelectorRecyclerViewAdapter.RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_select_questions, parent, false);
        return new QuestionSelectorRecyclerViewAdapter.RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull QuestionSelectorRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class RowHolder extends RecyclerView.ViewHolder {
        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void bind(Integer position) {
            // set question's text / this view only shows questions not choices or answers
            TextView questionInfo = itemView.findViewById(R.id.textQuestionListing);
            String questionText = itemView.getContext().getString(R.string.question) + ": " + questions.get(position).getQuestion();
            questionInfo.setText(questionText);
            // set selection pattern if there are already selected questions
            CheckBox questionSelect = itemView.findViewById(R.id.radioButtonQuestionListing);
            if (selected.contains(questions.get(position).getPrivateKey())) {
                questionSelect.setChecked(true);
            }
            // add or remove selected questions from set
            questionSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String key = questions.get(position).getPrivateKey();
                if (isChecked) {
                    selected.add(key);
                } else {
                    selected.remove(key);
                }
            });
        }
    }
}
