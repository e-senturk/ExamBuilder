package tr.edu.yildiz.ertugrulsenturk.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    /**
     * Class structure that stores the question's basic information and e-mail address of it's creator
     */
    private String privateKey;
    private final String question;
    private int answer;
    private ArrayList<String> choices;
    private final String userEmail;
    private final Attachment attachment;


    public Question(String privateKey, String userEmail, String question, ArrayList<String> choices, int answer, Attachment attachment) {
        this.privateKey = privateKey;
        this.userEmail = userEmail;
        this.question = question;
        this.answer = answer;
        this.choices = choices;
        this.attachment = attachment;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public String getUserEmail() {
        return userEmail;
    }

}
