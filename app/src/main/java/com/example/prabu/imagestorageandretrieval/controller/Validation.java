package com.example.prabu.imagestorageandretrieval.controller;
import android.widget.EditText;
import java.util.regex.Pattern;
/**
 * Created by Admin on 14/12/2017.
 */

public class Validation {
    private static final String PHONE_REGEX = "\\d{10}";
    private static final String PHONE_MSG = "##########";
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }
    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false


        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        };

        return true;
    }

}
