package com.commonmodule.mi.utils;

/**
 * Created by mind on 2/10/15.
 */
public class ValidationUtil {

    public static final boolean isValidString(String charSequence)
    {

        if (charSequence != null && !charSequence.trim().replace(" ","").isEmpty())
        {
            return true;
        }
        else {
            return false;
        }

    }

}
