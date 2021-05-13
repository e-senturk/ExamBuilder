package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.ExamDataBase;
import tr.edu.yildiz.ertugrulsenturk.service.tools.ExamTools;
import tr.edu.yildiz.ertugrulsenturk.service.tools.StorageTools;

public class ExamGeneratorActivity extends AppCompatActivity {
    /**
     * An activity for generating new exam
     */
    private User currentUser;
    private Exam selectedExam;
    private ArrayList<String> selectedQuestions;
    private NumberPicker numberPicker;
    private Spinner examCodeSpinner;
    private RadioGroup difficultyGroup;
    private TextView examIdInfo;
    private ArrayList<String> exams;
    private Button deleteExamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_generator);
        setTitle(R.string.create_exam);
        // read current user for accessing database
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("user");
        selectedQuestions = null;
        numberPicker = findViewById(R.id.timerPicker);
        examCodeSpinner = findViewById(R.id.examCodeSpinner);
        examIdInfo = findViewById(R.id.examIdInfoText);
        difficultyGroup = findViewById(R.id.radioGroupDifficulty);
        difficultyGroup.check(R.id.radioButtonDifficulty2);
        deleteExamButton = findViewById(R.id.deleteExamButton);
        numberPicker.setMaxValue(90);
        numberPicker.setMinValue(5);
        // read exam settings from shared preferences
        resetSettings();

        // exams object only stores exam names
        exams = ExamDataBase.getExamNames(this, MODE_PRIVATE, currentUser);
        if (exams == null) {
            exams = new ArrayList<>();
        }
        // setting for exam list spinner
        exams.add(0, getString(R.string.new_exam));
        ArrayAdapter<String> examAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                exams
        );
        examCodeSpinner.setAdapter(examAdapter);
        // selected fields must be updated when selecting a new exam
        examCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    examIdInfo.setText("");
                    examIdInfo.setEnabled(true);
                    deleteExamButton.setVisibility(View.INVISIBLE);
                    selectedExam = null;
                    selectedQuestions = null;
                    resetSettings();
                } else {
                    selectedExam = ExamDataBase.getSingleExam(getApplicationContext(), MODE_PRIVATE, exams.get(position), currentUser);
                    if (selectedExam != null) {
                        deleteExamButton.setVisibility(View.VISIBLE);
                        examIdInfo.setEnabled(false);
                        examIdInfo.setText(exams.get(position));
                        numberPicker.setValue(selectedExam.getExamTime());
                        difficultyGroup.check(difficultyGroup.getChildAt(selectedExam.getDifficulty() - 2).getId());
                        selectedQuestions = selectedExam.getQuestionIDList();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // opens question selector activity
    public void selectQuestion(View view) {
        Intent intent = new Intent(ExamGeneratorActivity.this, QuestionSelectActivity.class);
        intent.putExtra("user", currentUser);
        intent.putExtra("selected_questions", selectedQuestions);
        startActivityForResult(intent, 1);
    }

    // saves exam first and start the exam
    public void startExam(View view) {
        saveExam(view);
        if (selectedExam != null) {
            Intent intent = new Intent(ExamGeneratorActivity.this, ExamActivity.class);
            intent.putExtra("exam", selectedExam);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        }
    }

    // resets exam settings
    public void resetSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        numberPicker.setValue(sharedPreferences.getInt("exam_time", 30));
        int difficulty = sharedPreferences.getInt("difficulty", 5);
        difficultyGroup.check(difficultyGroup.getChildAt(difficulty - 2).getId());
    }

    // saves exam
    public void saveExam(View view) {
        String examID = examIdInfo.getText().toString();
        int examTime = numberPicker.getValue();
        int radioButtonID = difficultyGroup.getCheckedRadioButtonId();
        View radioButton = difficultyGroup.findViewById(radioButtonID);
        int difficulty = difficultyGroup.indexOfChild(radioButton) + 2;
        // validate and generate new exam object
        if (examID.equals("")) {
            Toast.makeText(this, getString(R.string.exam_code_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedQuestions == null || selectedQuestions.size() == 0) {
            Toast.makeText(this, getString(R.string.you_must_select_at_least_one_question), Toast.LENGTH_SHORT).show();
            return;
        } else if (examCodeSpinner.getSelectedItemPosition() == 0 && exams.contains(examID)) {
            Toast.makeText(this, getString(R.string.this_exam_id_already_exists), Toast.LENGTH_SHORT).show();
            return;
        }
        selectedExam = new Exam(examID, difficulty, examTime, selectedQuestions, currentUser.getEMail());
        // add reset or update fields after saving new exam
        if (examCodeSpinner.getSelectedItemPosition() == 0) {
            boolean success = ExamDataBase.addExam(this, MODE_PRIVATE, selectedExam, currentUser);
            exams.add(examID);
            if (success && view.getId() == R.id.saveExamButton) {
                Toast.makeText(this, getString(R.string.exam_saved), Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean success = ExamDataBase.updateExam(this, MODE_PRIVATE, selectedExam, currentUser);
            if (success && view.getId() == R.id.saveExamButton) {
                Toast.makeText(this, getString(R.string.exam_updated), Toast.LENGTH_SHORT).show();
            }
        }
        // share exam as txt file
        if (view == findViewById(R.id.shareExamButton)) {
            String examText = ExamTools.convertExamToString(this, MODE_PRIVATE, selectedExam, currentUser);
            String fileName = selectedExam.getExamCode() + "_" + currentUser.getUserFirstName().toLowerCase()
                    + "_" + currentUser.getUserLastName().toLowerCase() + ".txt";
            StorageTools.share(this, examText,
                    fileName,
                    getString(R.string.share_exam_as_file),
                    getString(R.string.share_exam_title));
        }
        // show delete button and disable examID editing because its a private key
        deleteExamButton.setVisibility(View.VISIBLE);
        examIdInfo.setEnabled(false);
        examCodeSpinner.setSelection(exams.size() - 1);
    }

    // deletes exam with an alert dialog
    public void deleteExam(View view) {
        if (selectedExam != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.exam_will_be_deleted));
            builder.setMessage(getString(R.string.are_you_sure_exam));
            builder.setNegativeButton(getString(R.string.no), null);
            builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                ExamDataBase.deleteExam(this, MODE_PRIVATE, selectedExam, currentUser);
                exams.remove(selectedExam.getExamCode());
                examCodeSpinner.setSelection(0);
            });
            builder.show();
            resetSettings();

        }
    }

    // gets selected questions from question select activity
    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                selectedQuestions = (ArrayList<String>) data.getSerializableExtra("selected");
        }
    }


}