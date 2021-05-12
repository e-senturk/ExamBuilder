package tr.edu.yildiz.ertugrulsenturk.service.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Attachment;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.Question;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.tools.BlobTools;

public class QuestionDataBase {
    /**
     * Sql database connection function for questions
     */
    private QuestionDataBase() {
    }

    // adds new question or creates new question table on sql
    public static void addQuestion(Context context, int mode, Question question) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("questions", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS questions (id VARCHAR PRIMARY KEY, question VARCHAR, choices BLOB, choice_count NUMBER, answer NUMBER, user_email VARCHAR, attachment VARCHAR)");
            String insert = "INSERT INTO questions (id, question, choices, choice_count, answer, user_email, attachment) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String idValue = java.util.UUID.randomUUID().toString();
            System.out.println(idValue);
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, idValue);
            sqLiteStatement.bindString(2, question.getQuestion());
            sqLiteStatement.bindBlob(3, BlobTools.arrayListToBlob(question.getChoices()));
            sqLiteStatement.bindLong(4, question.getChoices().size());
            sqLiteStatement.bindLong(5, question.getAnswer());
            sqLiteStatement.bindString(6, question.getUserEmail());
            // don't bind attachment id if its null
            if (question.getAttachment() != null) {
                sqLiteStatement.bindString(7, question.getAttachment().toString());
            }
            sqLiteStatement.execute();
            Toast.makeText(context, context.getString(R.string.question_saved), Toast.LENGTH_SHORT).show();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // updates given question on sql
    public static void updateQuestion(Context context, int mode, Question question, User user) {
        try {
            // if selected question is not created from current user then throw an exception
            if (!question.getUserEmail().equals(user.getEMail())) {
                Toast.makeText(context, context.getString(R.string.you_cant_change_this_question), Toast.LENGTH_SHORT).show();
                throw new Exception();
            }
            SQLiteDatabase database = context.openOrCreateDatabase("questions", mode, null);
            String update = "UPDATE questions SET question=?, choices=?,choice_count=?, answer=?, user_email=?, attachment=? WHERE id = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, question.getQuestion());
            sqLiteStatement.bindBlob(2, BlobTools.arrayListToBlob(question.getChoices()));
            sqLiteStatement.bindLong(3, question.getChoices().size());
            sqLiteStatement.bindLong(4, question.getAnswer());
            sqLiteStatement.bindString(5, user.getEMail());
            // don't bind attachment id if its null
            if (question.getAttachment() != null) {
                sqLiteStatement.bindString(6, question.getAttachment().toString());
            }
            sqLiteStatement.bindString(7, question.getPrivateKey());
            sqLiteStatement.execute();
            Toast.makeText(context, context.getString(R.string.question_updated), Toast.LENGTH_SHORT).show();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gets question from sql
    public static ArrayList<Question> getQuestions(Context context, int mode, User user) {
        ArrayList<Question> questions = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("questions", mode, null);
            Cursor cursor = database.rawQuery("SELECT id,question,choices,answer,user_email,attachment FROM questions WHERE user_email= ?", new String[]{user.getEMail()});
            int idIndex = cursor.getColumnIndex("id");
            int questionIndex = cursor.getColumnIndex("question");
            int choicesIndex = cursor.getColumnIndex("choices");
            int answerIndex = cursor.getColumnIndex("answer");
            int userEmailIndex = cursor.getColumnIndex("user_email");
            int attachmentIndex = cursor.getColumnIndex("attachment");
            while (cursor.moveToNext()) {
                String id = cursor.getString(idIndex);
                String userID = cursor.getString(userEmailIndex);
                String question = cursor.getString(questionIndex);
                ArrayList<String> choices = BlobTools.blobToArrayList(cursor.getBlob(choicesIndex));
                int answer = (int) cursor.getLong(answerIndex);
                String attachmentText = cursor.getString(attachmentIndex);
                Attachment attachment = null;
                // create new attachment from sql text
                if (attachmentText != null && attachmentText.length() != 0) {
                    attachment = new Attachment(attachmentText);
                }
                questions.add(new Question(id, userID, question, choices, answer, attachment));
            }
            cursor.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    // removes question from sql
    public static void deleteQuestion(Context context, int mode, Question question, User user) {
        try {
            // if selected question is not created from current user then throw an exception
            if (!question.getUserEmail().equals(user.getEMail())) {
                Toast.makeText(context, R.string.you_cant_delete_this_question, Toast.LENGTH_SHORT).show();
                throw new Exception();
            }
            SQLiteDatabase database = context.openOrCreateDatabase("questions", mode, null);
            String delete = "DELETE FROM questions WHERE id = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, question.getPrivateKey());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            Toast.makeText(context, context.getString(R.string.question_deleted), Toast.LENGTH_SHORT).show();
            database.close();
            // delete question from all exams as well
            // if exam has no remaining question then remove that exam too
            ArrayList<Exam> allExams = ExamDataBase.getAllExams(context, mode, user);
            for (Exam exam : allExams) {
                ArrayList<String> questionIDList = exam.getQuestionIDList();
                if (questionIDList.contains(question.getPrivateKey())) {
                    if (questionIDList.size() == 1) {
                        ExamDataBase.deleteExam(context, mode, exam, user);
                    } else {
                        questionIDList.remove(question.getPrivateKey());
                        exam.setQuestionIDList(questionIDList);
                        ExamDataBase.updateExam(context, mode, exam, user);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
