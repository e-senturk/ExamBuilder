package tr.edu.yildiz.ertugrulsenturk.service.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.Toast;

import tr.edu.yildiz.ertugrulsenturk.R;
import tr.edu.yildiz.ertugrulsenturk.model.User;

public class UserDataBase {
    /**
     * Sql database connection function for users
     */
    private UserDataBase() {
    }

    // adds new user or creates new user table on sql
    public static void addUser(Context context, int mode, User user) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("users", mode, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS users (first_name VARCHAR, last_name VARCHAR, phone_number VARCHAR, e_mail VARCHAR PRIMARY KEY, password VARCHAR, image BLOB)");
            String insert = "INSERT INTO users (first_name, last_name, phone_number, e_mail, password, image) VALUES (?, ?, ?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insert);
            sqLiteStatement.bindString(1, user.getUserFirstName());
            sqLiteStatement.bindString(2, user.getUserLastName());
            sqLiteStatement.bindString(3, user.getPhoneNumber());
            sqLiteStatement.bindString(4, user.getEMail());
            sqLiteStatement.bindString(5, user.getPassword());
            // don't bind user image if its null
            if (user.getUserImage() != null)
                sqLiteStatement.bindBlob(6, user.getUserImage());
            else
                sqLiteStatement.bindBlob(6, new byte[]{});
            sqLiteStatement.execute();
            sqLiteStatement.close();
            database.close();
            Toast.makeText(context, context.getString(R.string.user_created), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gets user with selected email and password
    public static User getUser(Context context, int mode, String email, String password) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("users", mode, null);
            Cursor cursor = database.rawQuery("SELECT first_name,last_name,phone_number,image,password FROM users WHERE e_mail = ?", new String[]{email});
            cursor.moveToNext();
            // if password is invalid throw an exception
            if (!password.equals(cursor.getString(cursor.getColumnIndex("password")))) {
                throw new Exception();
            }
            String firstName = cursor.getString(cursor.getColumnIndex("first_name"));
            String lastName = cursor.getString(cursor.getColumnIndex("last_name"));
            String phone_number = cursor.getString(cursor.getColumnIndex("phone_number"));
            byte[] imageInfo = cursor.getBlob(cursor.getColumnIndex("image"));
            cursor.close();
            database.close();
            return new User(firstName, lastName, phone_number, email, password, imageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // check first name + last name is already exists in sql
    public static boolean isExistingUser(Context context, int mode, String firstNameInput, String lastNameInput, String emailInput) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("users", mode, null);
            Cursor cursor = database.rawQuery("SELECT e_mail FROM users WHERE e_mail = ?", new String[]{emailInput});
            cursor.moveToNext();
            cursor.getString(cursor.getColumnIndex("e_mail"));
            Toast.makeText(context, context.getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();
            cursor.close();
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("users", mode, null);
            Cursor cursor = database.rawQuery("SELECT first_name FROM users WHERE first_name = ? AND last_name = ?", new String[]{firstNameInput, lastNameInput});
            cursor.moveToNext();
            cursor.getString(cursor.getColumnIndex("first_name"));
            Toast.makeText(context, context.getString(R.string.user_already_exists), Toast.LENGTH_SHORT).show();
            cursor.close();
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // update user in sql table
    public static void updateUser(Context context, int mode, User user) {
        try {
            SQLiteDatabase database = context.openOrCreateDatabase("users", mode, null);
            String update = "UPDATE users SET first_name=?, last_name=?, phone_number=?, e_mail=?, password=?, image=? WHERE e_mail = ?;";
            SQLiteStatement sqLiteStatement = database.compileStatement(update);
            sqLiteStatement.bindString(1, user.getUserFirstName());
            sqLiteStatement.bindString(2, user.getUserLastName());
            sqLiteStatement.bindString(3, user.getPhoneNumber());
            sqLiteStatement.bindString(4, user.getEMail());
            sqLiteStatement.bindString(5, user.getPassword());
            // don't bind user image if its null
            if (user.getUserImage() != null)
                sqLiteStatement.bindBlob(6, user.getUserImage());
            else
                sqLiteStatement.bindBlob(6, new byte[]{});
            sqLiteStatement.bindString(7, user.getEMail());
            sqLiteStatement.execute();
            sqLiteStatement.close();
            Toast.makeText(context, context.getString(R.string.user_updated), Toast.LENGTH_SHORT).show();
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
