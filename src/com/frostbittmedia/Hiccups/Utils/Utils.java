package com.frostbittmedia.Hiccups.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String getDateTimeString(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = simpleDateFormat.format(date);

        return currentTime;
    }
}
