package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.adapter.QuestionListRecyclerViewAdapter;
import tr.edu.yildiz.ertugrulsenturk.model.Attachment;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.QuestionDataBase;
import tr.edu.yildiz.ertugrulsenturk.service.tools.MediaTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.QuestionTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.StorageTools;

public class AddQuestionActivity extends AppCompatActivity {
    /**
     * An activity for adding or editing a question
     */
    Bitmap attachmentBitmap;
    String attachmentPath;
    private TextView newQuestionText;
    private RadioGroup newQuestionAnswerGroup;
    private LinearLayout choicesLayout;
    private ArrayList<LinearLayout> choices;
    private ArrayList<RadioButton> answers;
    private RadioGroup attachmentType;
    private User currentUser;
    private Question question;
    private int position;
    private VideoView videoView;
    private ImageView imageView;
    private Uri imageUri, videoUri, audioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        // in this layout we are not allowing to change rotation for avoid data loss
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setTitle(R.string.add_new_question);
        newQuestionText = findViewById(R.id.newQuestionText);
        newQuestionAnswerGroup = findViewById(R.id.newQuestionAnswerGroup);
        choicesLayout = findViewById(R.id.choicesLayout);
        videoView = findViewById(R.id.videoViewQuestion);
        imageView = findViewById(R.id.imageViewQuestion);
        attachmentType = findViewById(R.id.attachmentTypeRadioGroup);
        attachmentType.check(R.id.noneAttachmentRadioButton);
        // choices stores choice's linear layout
        choices = new ArrayList<>();
        // answers stores answer's radio button
        answers = new ArrayList<>();
        // attachment Bitmap stores imageView information
        attachmentBitmap = null;
        // stores attachment's path location
        attachmentPath = "";
        // we need to get user info
        // also we need question's order in questions list and question itself for editing
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");
        question = (Question) intent.getSerializableExtra("question");
        position = intent.getIntExtra("position", -1);
        // initialize choice layout
        addChoice(true);
        // if question is not null then we are in editing mode
        if (question != null) {
            updateFields();
        }
        // while clicking attachment type radio buttons we need to clear previous data
        attachmentType.setOnCheckedChangeListener((group, checkedId) -> {
            imageUri = null;
            videoUri = null;
            attachmentBitmap = null;
            videoView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.question_mark));
        });
    }

    // saves or updates a question
    public void saveNewQuestion(View view) {
        // validation for question
        if (newQuestionText.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.question_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return;
        } else if (choices.size() < 3) {
            Toast.makeText(this, getString(R.string.you_must_add_at_least_two_choices), Toast.LENGTH_SHORT).show();
            return;
        } else if (!QuestionTools.validateChoices(choices)) {
            Toast.makeText(this, getString(R.string.choices_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return;
        } else {
            // create none type attachment for empty attachment
            Attachment attachment = new Attachment("-", "none", "-");
            // check fields for correct attachment
            String attachmentTypeString = getAttachmentTypeString();
            // if attachment selected in correct form then create new attachment class
            if (!attachmentTypeString.equals("") && !attachmentPath.equals("")) {
                attachment = new Attachment(attachmentTypeString + "~" + attachmentPath);
            }
            // create new question from fields
            Question newQuestion = new Question("-1", currentUser.getEMail(), newQuestionText.getText().toString(), QuestionTools.getChoicesAsArrayList(choices), getSelectedAnswer(), attachment);
            // set questions private keys
            if (question != null) {
                newQuestion.setPrivateKey(question.getPrivateKey());
                QuestionDataBase.updateQuestion(this, MODE_PRIVATE, newQuestion, currentUser);
                QuestionListRecyclerViewAdapter.getQuestions().set(position, newQuestion);
                try {
                    Objects.requireNonNull(QuestionListActivity.getRecyclerView().getAdapter()).notifyDataSetChanged();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                QuestionDataBase.addQuestion(this, MODE_PRIVATE, newQuestion);
            }
        }
        // if question is not null then we are in editing mode we must finish activity
        if (question != null) {
            finish();
        } else {
            // if question is a new question then only clearUI
            clearUI();
        }
    }

    // checks necessary fields for finding valid attachment
    public void getAttachment(View view) {
        if (attachmentType.getCheckedRadioButtonId() == R.id.imageAttachmentRadioButton) {
            getImage();
        } else if (attachmentType.getCheckedRadioButtonId() == R.id.videoAttachmentRadioButton) {
            getVideo();
        } else if (attachmentType.getCheckedRadioButtonId() == R.id.audioAttachmentRadioButton) {
            getAudio();
        } else {
            imageUri = null;
            videoUri = null;
            attachmentBitmap = null;
            Toast.makeText(this, getString(R.string.select_a_type_first), Toast.LENGTH_SHORT).show();
        }
    }

    // intent for gallery to get video
    public void getVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        }
    }

    // intent for gallery to get image
    public void getImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 4);
        }
    }

    // intent for gallery to get audio
    public void getAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        } else {
            Intent intent = new Intent(new Intent(Intent.ACTION_GET_CONTENT));
            intent.setType("audio/*");
            startActivityForResult(intent, 6);
        }
    }


    // start intent's automatically after gathering permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        } else if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 4);
            }
        } else if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(new Intent(Intent.ACTION_GET_CONTENT));
                intent.setType("audio/*");
                startActivityForResult(intent, 6);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // after getting intent results fill fields with proper values
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        videoUri = null;
        audioUri = null;
        imageUri = null;
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            videoView.setBackground(null);
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            videoUri = data.getData();
            MediaTools.initializeVideoView(this, videoUri, videoView, false, false);
        }
        if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            videoView.setVideoURI(null);
            videoView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageUri = data.getData();
            attachmentBitmap = MediaTools.initializeImageView(this, imageUri, imageView);
        }
        if (requestCode == 6 && resultCode == RESULT_OK && data != null) {
            imageView.setImageBitmap(null);
            imageView.setVisibility(View.INVISIBLE);
            videoView.setVisibility(View.VISIBLE);
            audioUri = data.getData();
            MediaTools.initializeVideoView(this, audioUri, videoView, true, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // clear all ui elements
    public void clearUI() {
        newQuestionText.setText("");
        choices.clear();
        answers.clear();
        choicesLayout.removeAllViews();
        imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.question_mark));
        attachmentBitmap = null;
        imageUri = null;
        audioUri = null;
        videoUri = null;
        videoView.setVideoURI(null);
        videoView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        newQuestionAnswerGroup.removeAllViews();
        addChoice(true);
    }

    // update UI elements for editing old question
    public void updateFields() {
        String questionInfo = question.getQuestion();
        ArrayList<String> choicesInfo = question.getChoices();
        int answerInfo = question.getAnswer();
        newQuestionText.setText(questionInfo);
        for (String choice : choicesInfo) {
            // get last element and update its buttons image and text
            LinearLayout lastButton = choices.get(choices.size() - 1);
            ImageButton imageButton = (ImageButton) lastButton.getChildAt(0);
            // update + image to - image
            imageButton.setImageResource(R.drawable.remove);
            TextView textView = (TextView) lastButton.getChildAt(2);
            textView.setText(choice);
            // we need to set visible to last choice's input section
            textView.setVisibility(View.VISIBLE);
            // after editing last choice then we need to add new choice
            addChoice(false);
        }
        Attachment attachment = question.getAttachment();
        String type = attachment.getType();
        switch (type) {
            case "video":
                attachmentType.check(R.id.videoAttachmentRadioButton);
                break;
            case "audio":
                attachmentType.check(R.id.audioAttachmentRadioButton);
                break;
            case "image":
                attachmentType.check(R.id.imageAttachmentRadioButton);
                break;
        }
        MediaTools.initializeAttachment(this, attachment, videoView, imageView);
        // If choice count changes then select the first choice
        if (answers.size() != 0 && answers.size() > answerInfo) {
            newQuestionAnswerGroup.check(answers.get(answerInfo).getId());
        }

    }

    // returns selected question's answer value
    public int getSelectedAnswer() {
        return newQuestionAnswerGroup.indexOfChild(findViewById(newQuestionAnswerGroup.getCheckedRadioButtonId()));
    }

    // creates or removes new layout's for new choices
    public void addChoice(boolean initialize) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_question_choice, choicesLayout, false);
        // change previous option's image to -;
        TextView letter = (TextView) layout.getChildAt(1);
        String label = String.valueOf((char) (choices.size() + (int) 'A'));
        letter.setText(label);
        choices.add(layout);
        choicesLayout.addView(layout);
        if (!initialize) {
            RadioButton radioButton = new RadioButton(this);
            String labelRadio = String.valueOf((char) (answers.size() + (int) 'A'));
            radioButton.setText(labelRadio);
            radioButton.setWidth(newQuestionAnswerGroup.getWidth() / 5);
            answers.add(radioButton);
            newQuestionAnswerGroup.addView(radioButton);
        }
    }

    // choice adding or removing function
    public void choiceEditor(View view) {
        // get pressed buttons index
        LinearLayout parent = (LinearLayout) view.getParent();
        int index = choices.indexOf(parent);
        // if pressed button on the last element and choice count are less then 5 then add a new choice
        if (index == choices.size() - 1 && index < 5) {
            // get last element and update its buttons image and text
            LinearLayout lastButton = choices.get(choices.size() - 1);
            ImageButton imageButton = (ImageButton) lastButton.getChildAt(0);
            // update + image to - image
            imageButton.setImageResource(R.drawable.remove);
            TextView textView = (TextView) lastButton.getChildAt(2);
            // we need to set visible to last choice's input section
            textView.setVisibility(View.VISIBLE);
            // after editing last choice then we need to add new choice
            addChoice(false);
        }
        // if pressed button is not in the last choice then remove pressed choice
        else {
            // remove choice and answer from layout and tracking array
            choices.remove(index);
            answers.remove(index);
            choicesLayout.removeViewAt(index);
            newQuestionAnswerGroup.removeViewAt(index);
            // update choice and answer labels after remove
            for (int i = index; i < choices.size(); i++) {
                TextView letter = (TextView) choices.get(i).getChildAt(1);
                String newLetterText = String.valueOf((char) (letter.getText().toString().charAt(0) - 1));
                letter.setText(newLetterText);
            }
            for (int i = index; i < answers.size(); i++) {
                RadioButton radioButton = answers.get(i);
                String newLetterText = String.valueOf((char) (radioButton.getText().toString().charAt(0) - 1));
                radioButton.setText(newLetterText);
            }
        }
        // If choice count changes then select the first choice
        if (answers.size() != 0) {
            newQuestionAnswerGroup.check(answers.get(0).getId());
        }
    }

    // gets attachment type as a string from radio buttons
    private String getAttachmentTypeString() {
        if (attachmentType.getCheckedRadioButtonId() == R.id.imageAttachmentRadioButton) {
            if (attachmentBitmap != null && imageUri != null) {
                attachmentPath = StorageTools.storeAndGetPath(this, imageUri);
            }
            return "image";
        } else if (attachmentType.getCheckedRadioButtonId() == R.id.videoAttachmentRadioButton) {
            if (videoUri != null) {
                attachmentPath = StorageTools.storeAndGetPath(this, videoUri);
            }
            return "video";
        } else if (attachmentType.getCheckedRadioButtonId() == R.id.audioAttachmentRadioButton) {
            if (audioUri != null) {
                attachmentPath = StorageTools.storeAndGetPath(this, audioUri);
                return "audio";
            }
        }
        return "none";
    }
}