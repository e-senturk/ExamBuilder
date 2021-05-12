package tr.edu.yildiz.ertugrulsenturk.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Exam implements Serializable {
    /**
     * Class structure that stores the question's basic information and e-mail address of it's creator
     */
    private final String examCode;
    private final int difficulty;
    private final int examTime;
    private final String userID;
    private ArrayList<String> questionIDList;

    public Exam(String examCode, int difficulty, int examTime, ArrayList<String> questionIDList, String userID) {
        this.examCode = examCode;
        this.difficulty = difficulty;
        this.examTime = examTime;
        this.questionIDList = questionIDList;
        this.userID = userID;
    }

    public String getExamCode() {
        return examCode;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getExamTime() {
        return examTime;
    }

    public ArrayList<String> getQuestionIDList() {
        return questionIDList;
    }

    public void setQuestionIDList(ArrayList<String> questionIDList) {
        this.questionIDList = questionIDList;
    }

    public String getUserID() {
        return userID;
    }
}
