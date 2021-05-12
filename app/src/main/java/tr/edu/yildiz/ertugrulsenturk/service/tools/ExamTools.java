package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.database.QuestionDataBase;

public class ExamTools {
    /**
     * Auxiliary class for creating information from exam class
     */
    private ExamTools() {
    }

    // prints exam as a string in a proper format
    public static String convertExamToString(Context context, int mode, Exam exam, User user) {
        Set<String> questionsSelectedID = new HashSet<>(exam.getQuestionIDList());
        ArrayList<Question> questionsAll = QuestionDataBase.getQuestions(context, mode, user);
        StringBuilder questions = new StringBuilder();
        StringBuilder answers = new StringBuilder();
        answers.append(context.getString(R.string.answers)).append("\n");
        int i = 0;
        for (Question question : questionsAll) {
            if (questionsSelectedID.contains(question.getPrivateKey())) {
                Question questionWithDifficulty = QuestionTools.setQuestionDifficulty(question, exam.getDifficulty());
                String singleQuestionText = QuestionTools.convertQuestionToText(questionWithDifficulty);
                questions.append(context.getString(R.string.question))
                        .append(" ").append(i + 1).append("-) ")
                        .append(singleQuestionText).append("\n");
                answers.append(i + 1).append("-) ").append((char) (question.getAnswer() + 'A')).append("\t");
                i++;
            }
        }
        questions.append("\n\n").append(answers);
        return questions.toString();
    }

    // exam object only stores question's id and this class gets question list from exam object
    public static ArrayList<Question> convertExamToQuestions(Context context, int mode, Exam exam, User user) {
        Set<String> questionsSelectedID = new HashSet<>(exam.getQuestionIDList());
        ArrayList<Question> questionsAll = QuestionDataBase.getQuestions(context, mode, user);
        ArrayList<Question> examList = new ArrayList<>();
        // check all questions and pick ones in the exam
        for (Question question : questionsAll) {
            if (questionsSelectedID.contains(question.getPrivateKey())) {
                Question questionWithDifficulty = QuestionTools.setQuestionDifficulty(question, exam.getDifficulty());
                examList.add(questionWithDifficulty);
            }
        }
        return examList;
    }
}
