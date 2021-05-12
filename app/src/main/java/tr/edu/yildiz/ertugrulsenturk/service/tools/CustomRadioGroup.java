package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.widget.RadioButton;

import java.util.ArrayList;

public class CustomRadioGroup {
    /**
     * Auxiliary class for creating custom radio group to group and manage radio buttons on the exam
     */
    // radio buttons storing in a arraylist
    private final ArrayList<RadioButton> radioButtons;
    // a button is selected or not requires for empty selection
    private boolean checked;
    // selected index
    private int checkedIndex;

    public CustomRadioGroup() {
        this.radioButtons = new ArrayList<>();
        checked = false;
        // checked index -1 means question is empty
        checkedIndex = -1;
    }

    // reads all radiobutton groups and generate checked button's index list
    public static ArrayList<Integer> radioButtonsToSelectedChoices(ArrayList<CustomRadioGroup> customRadioGroups) {
        ArrayList<Integer> selectedChoices = new ArrayList<>();
        for (CustomRadioGroup customRadioGroup : customRadioGroups) {
            selectedChoices.add(customRadioGroup.getCheckedIndex());
            System.out.println(customRadioGroup.getCheckedIndex());
        }
        return selectedChoices;
    }

    public int getCheckedIndex() {
        return checkedIndex;
    }

    public void add(RadioButton radioButton) {
        radioButtons.add(radioButton);
        radioButton.setOnClickListener(v -> {
            int index = radioButtons.indexOf(radioButton);
            // checked value set based on the clicked button is selected or not
            checked = radioButton.isChecked();
            if (checked) {
                // if radio button checked set all radio buttons non checked
                for (RadioButton button : radioButtons) {
                    button.setChecked(false);
                }
                // if checked index is not the same with previous checked index then select radio button
                if (index != checkedIndex) {
                    radioButton.setChecked(true);
                    checkedIndex = index;
                }
                // otherwise not select any radio button and set checked index -1;
                else {
                    checkedIndex = -1;
                }
                // set checked value to false for next selection
                checked = false;
            }
        });
    }
}
