package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Question;

public class QuestionTools {
    /**
     * Auxiliary class for question objects
     */
    private QuestionTools() {
    }

    // choices normally storing as a linear layout's 3rd element this
    // function generates an arraylist from these linear layouts
    public static ArrayList<String> getChoicesAsArrayList(ArrayList<LinearLayout> choices) {
        ArrayList<String> choiceList = new ArrayList<>();
        for (LinearLayout choice : choices) {
            // Get input string from choice
            TextView choiceInput = (TextView) choice.getChildAt(2);
            choiceList.add(choiceInput.getText().toString());
        }
        choiceList.remove(choiceList.size() - 1);
        return choiceList;
    }

    // choices must be not empty but last choice is invisible in our design so it must be empty
    public static boolean validateChoices(ArrayList<LinearLayout> choices) {
        for (int i = 0; i < choices.size() - 1; i++) {
            TextView choice = (TextView) choices.get(i).getChildAt(2);
            if (choice.getText().toString().equals("")) {
                return false;
            }
        }
        return true;
    }

    // formatting choices as a single string for viewing in a non editable list
    public static String formatChoices(ArrayList<String> choices) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (; i < choices.size() - 1; i++) {
            stringBuilder.append((char) ('A' + i)).append("-) ").append(choices.get(i)).append("\n");
        }
        stringBuilder.append((char) ('A' + i)).append("-) ").append(choices.get(i));
        return stringBuilder.toString();
    }

    // formatting answer by adding a answer text for viewing in a list
    public static String formatAnswer(int choice, Context context) {
        char letter = (char) ('A' + choice);
        return context.getString(R.string.answer) + " " + letter;
    }

    // compare selected choices and correct answers and calculate results as a int array
    public static int[] calculateResults(ArrayList<Integer> selectedChoices, ArrayList<Question> questions) {
        int[] results = new int[]{0, 0, 0};
        int i = 0;
        for (; i < selectedChoices.size(); i++) {
            System.out.println("Clicked" + selectedChoices.get(i));
            System.out.println("Answer" + questions.get(i).getAnswer());
            if (selectedChoices.get(i) == -1) {
                results[2]++;
            } else if (selectedChoices.get(i) == questions.get(i).getAnswer()) {
                results[0]++;
            } else {
                results[1]++;
            }
        }
        for (; i < questions.size(); i++) {
            results[2]++;
        }
        return results;
    }

    // arrange question object and convert it a single string
    public static String convertQuestionToText(Question question) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(question.getQuestion()).append("\n");
        ArrayList<String> choices = question.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            stringBuilder.append((char) (i + 'A')).append(") ").append(choices.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }

    // modify a question with given difficulty
    public static Question setQuestionDifficulty(Question question, int difficulty) {
        Random random = new Random();
        ArrayList<String> choices = question.getChoices();
        // generate new choice array
        ArrayList<String> newChoices = new ArrayList<>();
        // get answer's index
        String answer = choices.get(question.getAnswer());
        // add answer into the new array so make sure its on the new choice list
        newChoices.add(answer);
        // remove answer from original choices for avoid duplication
        choices.remove(answer);
        int i = 0;
        // add each choice in random order with size of difficulty
        // or until choices get empty
        while (i < difficulty - 1 && !choices.isEmpty()) {
            int index = random.nextInt(choices.size());
            newChoices.add(choices.get(index));
            choices.remove(index);
            i++;
        }
        // shuffle new choices
        Collections.shuffle(newChoices);
        // find new answers index
        int answerIndex = newChoices.indexOf(answer);
        // update question
        question.setChoices(newChoices);
        question.setAnswer(answerIndex);
        return question;
    }
}
