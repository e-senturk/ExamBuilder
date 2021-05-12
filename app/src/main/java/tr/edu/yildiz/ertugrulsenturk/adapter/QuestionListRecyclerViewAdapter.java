package tr.edu.yildiz.ertugrulsenturk.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Attachment;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.QuestionDataBase;
import tr.edu.yildiz.ertugrulsenturk.service.tools.DialogTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.MediaTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.QuestionTools;
import tr.edu.yildiz.ertugrulsenturk.view.activity.AddQuestionActivity;

import static android.content.Context.MODE_PRIVATE;

public class QuestionListRecyclerViewAdapter extends RecyclerView.Adapter<QuestionListRecyclerViewAdapter.RowHolder> {
    /**
     * Recycler view adapter for listing questions in question list activity
     **/
    private static ArrayList<Question> questions;
    private final User currentUser;

    // questions stores all the questions by created that user
    // currentUser stores the logged in user
    public QuestionListRecyclerViewAdapter(ArrayList<Question> questions, User currentUser) {
        QuestionListRecyclerViewAdapter.questions = questions;
        this.currentUser = currentUser;
    }

    public static ArrayList<Question> getQuestions() {
        return questions;
    }

    @NonNull
    @Override
    public QuestionListRecyclerViewAdapter.RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_list_questions, parent, false);
        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListRecyclerViewAdapter.RowHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }


    public class RowHolder extends RecyclerView.ViewHolder implements Serializable {
        // stores color list for each question
        private final int[] colors = new int[]{
                itemView.getContext().getColor(R.color.bitter_sweet),
                itemView.getContext().getColor(R.color.medium_turquoise),
                itemView.getContext().getColor(R.color.midnight_green),
                itemView.getContext().getColor(R.color.mint_cream),
                itemView.getContext().getColor(R.color.naples_yellow)};
        TextView questionTextView;
        TextView choicesTextView;
        TextView answerTextView;
        ConstraintLayout questionLayout;
        ImageButton showAnswerButton;
        ImageButton deleteButton;
        ImageButton editButton;
        ImageButton attachmentButton;
        // stores either answer is visible or not
        boolean visibleAnswer;

        public RowHolder(@NonNull View itemView) {
            super(itemView);
        }


        public void bind(Integer position) {
            // call necessary find view by id functions
            initializeFields();
            // add onclick functions for the buttons
            clickFunctions(position);
            visibleAnswer = false;
            // set choices text
            choicesTextView.setText(QuestionTools.formatChoices(questions.get(position).getChoices()));
            // if login user is not the one who creates the question then hide edit and delete buttons
            if (!questions.get(position).getUserEmail().equals(currentUser.getEMail())) {
                editButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
            }
            // set question text
            String question = itemView.getContext().getString(R.string.question) + ": " + questions.get(position).getQuestion();
            questionTextView.setText(question);
            // set blurry the answer
            MediaTools.applyBlurMaskFilter(answerTextView, 2);
            // set answer text
            answerTextView.setText(QuestionTools.formatAnswer(questions.get(position).getAnswer(), itemView.getContext()));
            // set question's background
            questionLayout.setBackgroundColor(colors[position % colors.length]);
        }

        public void initializeFields() {
            questionLayout = itemView.findViewById(R.id.questionConstraintLayout);
            questionTextView = itemView.findViewById(R.id.textExamQuestion);
            choicesTextView = itemView.findViewById(R.id.choicesRecycler);
            answerTextView = itemView.findViewById(R.id.textAnswer);
            showAnswerButton = itemView.findViewById(R.id.questionShowAnswer);
            deleteButton = itemView.findViewById(R.id.questionDeleteButton);
            editButton = itemView.findViewById(R.id.questionEditButton);
            attachmentButton = itemView.findViewById(R.id.questionAttachment);
        }

        public void clickFunctions(Integer position) {
            // deletes question with an alert dialog
            deleteButton.setOnClickListener(v -> alertDialog(position));
            // open's question in add question activity for editing
            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), AddQuestionActivity.class);
                intent.putExtra("question", questions.get(position));
                intent.putExtra("user", currentUser);
                intent.putExtra("position", position);
                itemView.getContext().startActivity(intent);
            });
            // changes blurry view on the answer
            showAnswerButton.setOnClickListener(v -> {
                if (visibleAnswer) {
                    showAnswerButton.setImageResource(R.drawable.visible);
                    MediaTools.applyBlurMaskFilter(answerTextView, 3);
                    visibleAnswer = false;
                } else {
                    showAnswerButton.setImageResource(R.drawable.hidden);
                    MediaTools.applyBlurMaskFilter(answerTextView, Integer.MAX_VALUE);
                    visibleAnswer = true;
                }
            });
            // open's attachment in a dialog
            attachmentButton.setOnClickListener(v -> {
                Attachment attachment = questions.get(position).getAttachment();
                switch (attachment.getType()) {
                    case "video":
                        DialogTools.videoDialog(itemView.getContext(), attachment.getPath(), false, true);
                        break;
                    case "audio":
                        DialogTools.videoDialog(itemView.getContext(), attachment.getPath(), true, true);
                        break;
                    case "image":
                        DialogTools.imageDialog(itemView.getContext(), attachment.getPath());
                        break;
                }
            });
        }

        // shows an alert dialog before delete
        protected void alertDialog(Integer position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle(itemView.getContext().getString(R.string.question_will_be_deleted));
            builder.setMessage(itemView.getContext().getString(R.string.are_you_sure_question));
            builder.setNegativeButton(itemView.getContext().getString(R.string.no), null);
            builder.setPositiveButton(itemView.getContext().getString(R.string.yes), (dialogInterface, i) -> {
                QuestionDataBase.deleteQuestion(itemView.getContext(), MODE_PRIVATE, questions.get(position), currentUser);
                questions.remove(position.intValue());
                notifyDataSetChanged();
            });
            builder.show();
        }
    }


}