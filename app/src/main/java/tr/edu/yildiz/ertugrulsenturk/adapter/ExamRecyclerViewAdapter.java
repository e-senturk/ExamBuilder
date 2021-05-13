package tr.edu.yildiz.ertugrulsenturk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Attachment;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.service.tools.CustomRadioGroup;
import tr.edu.yildiz.ertugrulsenturk.service.tools.MediaTools;

public class ExamRecyclerViewAdapter extends RecyclerView.Adapter<ExamRecyclerViewAdapter.RowHolder> {
    /**
     * Recycler view adapter for listing questions in exam activity
     **/
    private static ArrayList<Question> questions;
    private static ArrayList<CustomRadioGroup> customRadioGroups;

    // questions stores all the questions in current exam
    // customRadioGroups stores all question's choice's radio buttons
    public ExamRecyclerViewAdapter(ArrayList<Question> questions) {
        ExamRecyclerViewAdapter.questions = questions;
        customRadioGroups = new ArrayList<>();
    }

    public static ArrayList<Integer> getSelectedChoices() {
        return CustomRadioGroup.radioButtonsToSelectedChoices(customRadioGroups);
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_exam_questions, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ExamRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class RowHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;
        TextView questionText;
        TextView questionNumber;
        LinearLayout choicesLayout;
        CustomRadioGroup customRadioGroup;

        public RowHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public void bind(Integer position) {
            // call necessary find view by id functions
            initializeFields();
            // set question number
            questionNumber.setText(String.valueOf(position + 1));
            // set question text
            questionText.setText(questions.get(position).getQuestion());
            // group choices with custom radio group
            groupRadioButtons(position);
            // initialize attachment
            Attachment attachment = questions.get(position).getAttachment();
            MediaTools.initializeAttachment(itemView.getContext(), attachment, videoView, imageView);
            if (attachment.getType().equals("none")) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
            }

        }

        public void initializeFields() {
            imageView = itemView.findViewById(R.id.examImageView);
            videoView = itemView.findViewById(R.id.examVideoView);
            questionText = itemView.findViewById(R.id.textExamQuestion);
            questionNumber = itemView.findViewById(R.id.textExamQuestionNumber);
            choicesLayout = itemView.findViewById(R.id.examChoicesLayout);
        }

        // group choice's radio buttons by using a custom radio group class and set choice's texts
        public void groupRadioButtons(Integer position) {
            customRadioGroup = new CustomRadioGroup();
            customRadioGroups.add(customRadioGroup);
            int i = 0;
            for (String choice : questions.get(position).getChoices()) {
                // inflate layout for single choice
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_exam_choice, choicesLayout, false);
                // set radio button's text
                RadioButton choiceButton = (RadioButton) layout.getChildAt(0);
                choiceButton.setText(String.valueOf((char) ('A' + i)));
                // add radiobutton on a custom group
                customRadioGroup.add(choiceButton);
                // set choice's text into textview
                TextView choiceInfo = (TextView) layout.getChildAt(1);
                choiceInfo.setText(choice);
                choicesLayout.addView(layout, i);
                i++;
            }
        }
    }
}
