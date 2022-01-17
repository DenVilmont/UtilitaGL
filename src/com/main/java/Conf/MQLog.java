package com.main.java.Conf;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MQLog {
    private static JTextArea instance = new JTextArea();
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");

    private MQLog(){}

    public static JTextArea getInstance(){
        return instance;
    }

    public static void write(String str){
        Date date = Calendar.getInstance().getTime();
        instance.append(sdf.format(date) + " :: " + str);
        instance.append(System.lineSeparator());
    }
}
