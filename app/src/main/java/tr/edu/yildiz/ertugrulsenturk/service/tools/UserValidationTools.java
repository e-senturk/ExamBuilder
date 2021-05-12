package tr.edu.yildiz.ertugrulsenturk.service.tools;

import android.content.Context;
import android.widget.Toast;

import java.io.InvalidObjectException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tr.edu.yildiz.ertugrulsenturk.R;

public class UserValidationTools {
    /**
     * Auxiliary class for managing for validation of user information
     */
    private UserValidationTools() {
    }

    // validates user name fields
    public static String validateName(Context context, String userName, int min, int max, String type) throws InvalidObjectException {
        // name can't be smaller than given min value
        if (userName.length() < min) {
            if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_name_too_short), Toast.LENGTH_SHORT).show();
            else if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_surname_too_short), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException(type + "_too_short");
        }
        // name can't be larger than given max value
        if ((userName.length() > max)) {
            if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_name_too_long), Toast.LENGTH_SHORT).show();
            else if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_surname_too_long), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException(type + "_too_long");
        }
        // name can't have digits or special characters
        Pattern pattern = Pattern.compile("^[\\p{L}\\p{Zs}]+$");
        Matcher matcher = pattern.matcher(userName);
        if (!matcher.matches()) {
            if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_name_invalid_character), Toast.LENGTH_SHORT).show();
            else if (type.matches("user_first_name"))
                Toast.makeText(context, context.getString(R.string.user_surname_invalid_character), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException(type + "_invalid_character");
        }
        return userName;
    }

    // call's validate name to validate user firstName
    public static String validateUserFirstName(Context context, String firstName) throws InvalidObjectException {
        return validateName(context, firstName, 3, 15, "user_first_name");
    }

    // call's validate name to validate user lastName
    public static String validateUserLastName(Context context, String lastName) throws InvalidObjectException {
        return validateName(context, lastName, 3, 30, "user_last_name");
    }

    // validates phone number
    public static String validatePhoneNumber(Context context, String phoneNumber) throws InvalidObjectException {
        // phone number can't be smaller than 7 digits
        if (phoneNumber.length() < 7) {
            Toast.makeText(context, context.getString(R.string.user_phone_number_invalid_phone_format), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException("user_phone_number_invalid_phone_format");
        }
        return phoneNumber;
    }

    // validates user e-mail
    public static String validateUserEmail(Context context, String eMail) throws InvalidObjectException {
        // e-mail must contains @ character
        if (!eMail.contains("@")) {
            Toast.makeText(context, context.getString(R.string.user_email_invalid_format), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException("user_email_invalid_format");
        }
        return eMail;
    }

    // validates password
    public static String validatePassword(Context context, String password) throws InvalidObjectException {
        // password must have at least 1 bigger, 1 smaller and 1 numerical characters
        Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*");
        Matcher matcher = pattern.matcher(password);
        System.out.println(password);
        // password must be at least 8 character long
        if (password.length() < 8 || !matcher.matches()) {
            Toast.makeText(context, context.getString(R.string.user_invalid_password_format), Toast.LENGTH_LONG).show();
            throw new InvalidObjectException("user_invalid_password_format");
        }
        return password;
    }

    // compares two password input and checks if they match or not
    public static void validatePasswordRe(Context context, String password, String passwordRe) throws InvalidObjectException {
        if (!password.equals(passwordRe)) {
            Toast.makeText(context, context.getString(R.string.user_password_repeat), Toast.LENGTH_SHORT).show();
            throw new InvalidObjectException("user_invalid_password_repeat");
        }
    }
}
