package com.hb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hb on 05/08/2018.
 */
public class Utils {

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


}
