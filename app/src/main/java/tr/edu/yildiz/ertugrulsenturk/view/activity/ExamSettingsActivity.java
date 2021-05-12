package tr.edu.yildiz.ertugrulsenturk.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.ertugrulsenturk.R;

public class ExamSettingsActivity extends AppCompatActivity {
    NumberPicker questionTimePicker;
    RadioGroup difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);
        setTitle(R.string.exam_settings_2);

        questionTimePicker = findViewById(R.id.timerPickerSettings);
        difficulty = findViewById(R.id.radioGroupDifficultySettings);
        questionTimePicker.setMinValue(5);
        questionTimePicker.setMaxValue(90);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int examTimeValue = sharedPreferences.getInt("exam_time", 30);
        int difficultyLevel = sharedPreferences.getInt("difficulty", 5);
        difficulty.check(difficulty.getChildAt(difficultyLevel - 2).getId());
        questionTimePicker.setValue(examTimeValue);
    }

    public void saveSettings(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("exam_time", questionTimePicker.getValue());
        int radioButtonID = difficulty.getCheckedRadioButtonId();
        View radioButton = difficulty.findViewById(radioButtonID);
        int checked = difficulty.indexOfChild(radioButton);
        editor.putInt("difficulty", checked + 2);
        editor.apply();
        Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
        finish();
    }
}