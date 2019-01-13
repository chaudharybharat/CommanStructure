package com.commonmodule.mi.utils;

public class Validation {

    public static final String REQUIRED_FILEDS = "All fields are mandatory.";
    public static final String INVALID_EMAIL = "Email is invalid.";
    public static final String REQUIRED_USER_NAME = "Username is invalid.";
    public static final String REQUIRED_EMAIL_USER_NAME = "Username/Email is invalid.";
    public static final String REQUIRED_PASSWORD = "password is required.";
    public static final String VALID_PASSWORD = "Password must be at least 8 characters.";

    public static boolean isEmailValid(CharSequence email) {
        if (!Validation.isRequiredField(email.toString()))
        {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isRequiredField(String strText) {
        return strText != null && !strText.trim().isEmpty();
    }

    public static int getIntFromString(final String str)
    {
        if (Validation.isRequiredField(str))
        {
            try {
                return Integer.parseInt(str);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return 0;
    }

}
