package com.example.ishow.Utils.Interface;

/**
 * Created by MRME on 2016-04-26.
 */
public class TextUtil {

    public static String splitCharOfText(String text,boolean name){
        if (text.contains("?"))
        {
            if (name)
                return "ishow student";
            else return "ishow school";
        }
        return text;
    }
}
