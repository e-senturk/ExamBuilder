package tr.edu.yildiz.ertugrulsenturk.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import tr.edu.yildiz.ertugrulsenturk.R;

public class ResultFragment extends Fragment {
    private final String userName;
    private final String correct;
    private final String incorrect;
    private final String unanswered;
    private final String grade;

    public ResultFragment(String userName, int correct, int incorrect, int unanswered) {
        this.userName = userName;
        this.correct = String.valueOf(correct);
        this.incorrect = String.valueOf(incorrect);
        this.unanswered = String.valueOf(unanswered);
        this.grade = String.valueOf((correct * 100) / (correct + incorrect + unanswered));
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_results, container, false);
        TextView correctInfo = viewGroup.findViewById(R.id.resultsCorrectAnswer);
        TextView incorrectInfo = viewGroup.findViewById(R.id.resultsIncorrectAnswer);
        TextView unansweredInfo = viewGroup.findViewById(R.id.resultsUnanswered);
        TextView gradeInfo = viewGroup.findViewById(R.id.resultGrade);
        TextView congrats = viewGroup.findViewById(R.id.congratsResultText);
        correctInfo.setText(correct);
        incorrectInfo.setText(incorrect);
        unansweredInfo.setText(unanswered);
        gradeInfo.setText(grade);
        String congratsText = getString(R.string.congrats) + ", " + userName + "!\n" + getString(R.string.you_have_completed);
        congrats.setText(congratsText);
        return viewGroup;
    }
}
