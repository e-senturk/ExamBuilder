package tr.edu.yildiz.ertugrulsenturk.service.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import java.util.ArrayList;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.Exam;
import tr.edu.yildiz.ertugrulsenturk.model.User;
import tr.edu.yildiz.ertugrulsenturk.service.tools.BlobTools;

public class ExamDataBase {
    /**
     * Sql database connection function for exams
     */
    private ExamDataBase() {
    }

    // adds new exam or creates new exam table on sql
    public static boolean addExam(Context context, int mode, Exam exam, User user) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS exams (exam_code VARCHAR PRIMARY KEY, difficulty NUMBER, exam_time NUMBER, user_id VARCHAR,question_id_list BLOB)");
            String insert = "INSERT INTO exams (exam_code, difficulty, exam_time, user_id, question_id_list) VALUES (?, ?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, exam.getExamCode());
            sqLiteStatement.bindLong(2, exam.getDifficulty());
            sqLiteStatement.bindLong(3, exam.getExamTime());
            sqLiteStatement.bindString(4, user.getEMail());
            // don't bind question id if its null
            if (exam.getQuestionIDList() != null)
                sqLiteStatement.bindBlob(5, BlobTools.arrayListToBlob(exam.getQuestionIDList()));
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // updates exam in sql
    public static boolean updateExam(Context context, int mode, Exam exam, User user) {
        try {
            // if selected exam is not created from current user then throw an exception
            if (!exam.getUserID().equals(user.getEMail())) {
                Toast.makeText(context, context.getString(R.string.you_cant_change_this_exam), Toast.LENGTH_SHORT).show();
                throw new Exception();
            }
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            String insert = "UPDATE exams SET difficulty = ?, exam_time = ?, question_id_list=? WHERE exam_code=? AND user_id=?";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindLong(1, exam.getDifficulty());
            sqLiteStatement.bindLong(2, exam.getExamTime());
            // don't bind question id if its null
            if (exam.getQuestionIDList() != null) {
                sqLiteStatement.bindBlob(3, BlobTools.arrayListToBlob(exam.getQuestionIDList()));
            }
            sqLiteStatement.bindString(4, exam.getExamCode());
            sqLiteStatement.bindString(5, user.getEMail());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // generates arraylist of all exam names from sql
    public static ArrayList<String> getExamNames(Context context, int mode, User user) {
        ArrayList<String> exam_codes = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            Cursor cursor = database.rawQuery("SELECT exam_code,question_id_list FROM exams WHERE user_id=?", new String[]{user.getEMail()});
            while (cursor.moveToNext()) {
                String exam_code = cursor.getString(cursor.getColumnIndex("exam_code"));
                exam_codes.add(exam_code);
            }
            cursor.close();
            database.close();
            return exam_codes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // gets single exam with given exam code from sql
    public static Exam getSingleExam(Context context, int mode, String exam_code, User user) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            Cursor cursor = database.rawQuery("SELECT difficulty, exam_time, question_id_list FROM exams WHERE user_id=? AND exam_code=?", new String[]{user.getEMail(), exam_code});
            int difficultyIndex = cursor.getColumnIndex("difficulty");
            int examTimeIndex = cursor.getColumnIndex("exam_time");
            int questionIDListIndex = cursor.getColumnIndex("question_id_list");
            cursor.moveToNext();
            int difficulty = cursor.getInt(difficultyIndex);
            int examTime = cursor.getInt(examTimeIndex);
            byte[] questionIDListBlob = cursor.getBlob(questionIDListIndex);
            ArrayList<String> questionIDList = BlobTools.blobToArrayList(questionIDListBlob);
            cursor.close();
            database.close();
            return new Exam(exam_code, difficulty, examTime, questionIDList, user.getEMail());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // gets all exams from sql
    public static ArrayList<Exam> getAllExams(Context context, int mode, User user) {
        ArrayList<Exam> exams = new ArrayList<>();
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            Cursor cursor = database.rawQuery("SELECT exam_code, difficulty, exam_time, question_id_list FROM exams WHERE user_id=?", new String[]{user.getEMail()});
            int examCodeIndex = cursor.getColumnIndex("difficulty");
            int difficultyIndex = cursor.getColumnIndex("difficulty");
            int examTimeIndex = cursor.getColumnIndex("exam_time");
            int questionIDListIndex = cursor.getColumnIndex("question_id_list");
            while (cursor.moveToNext()) {
                String examCode = cursor.getString(examCodeIndex);
                int difficulty = cursor.getInt(difficultyIndex);
                int examTime = cursor.getInt(examTimeIndex);
                byte[] questionIDListBlob = cursor.getBlob(questionIDListIndex);
                ArrayList<String> questionIDList = BlobTools.blobToArrayList(questionIDListBlob);
                exams.add(new Exam(examCode, difficulty, examTime, questionIDList, user.getEMail()));
            }
            cursor.close();
            database.close();
            return exams;
        } catch (Exception e) {
            e.printStackTrace();
            return exams;
        }
    }

    // deletes an exam from sql
    public static void deleteExam(Context context, int mode, Exam exam, User user) {
        try {
            // if selected exam is not created from current user then throw an exception
            if (!exam.getUserID().equals(user.getEMail())) {
                Toast.makeText(context, R.string.you_cant_delete_this_exam, Toast.LENGTH_SHORT).show();
                throw new Exception();
            }
            SQLiteDatabase database = context.openOrCreateDatabase("exams", mode, null);
            String delete = "DELETE FROM exams WHERE exam_code = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(delete);
            sqLiteStatement.bindString(1, exam.getExamCode());
            sqLiteStatement.execute();
            Toast.makeText(context, context.getString(R.string.exam_deleted), Toast.LENGTH_SHORT).show();
            sqLiteStatement.close();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}