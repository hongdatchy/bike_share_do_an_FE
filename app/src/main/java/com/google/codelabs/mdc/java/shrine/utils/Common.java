package com.google.codelabs.mdc.java.shrine.utils;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Common {

    public static void switchActivity(AppCompatActivity currentActivity, Class<?> nextActivity){
        Intent myIntent = new Intent(currentActivity, nextActivity);
        currentActivity.startActivity(myIntent);
    }

    public static Gson getMyGson(){
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
    }

    public static List<Integer> getListYear(){
        Calendar calendar = Calendar.getInstance();
        int nowYear = calendar.get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<>();
        for(int i = nowYear; i>= Constant.BEGIN_YEAR; i--){
            yearList.add(i);
        }
        return yearList;
    }

    public static List<Integer> getListMonth(){
        List<Integer> monthList = new ArrayList<>();
        for(int i = 1; i<= 12; i++){
            monthList.add(i);
        }
        return monthList;
    }
    public static List<Integer> getListDay(){
        List<Integer> dayList = new ArrayList<>();
        for(int i = 1; i<= 31; i++){
            dayList.add(i);
        }
        return dayList;
    }


    public static Date integerToDate(String year, String month, String day) throws ParseException {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        dateFormat.setLenient(false);
        date = dateFormat.parse(year +"/"+ month +"/"+ day);
        return date;
    }

    public static boolean checkCorrectDate(String year, String month, String day){
        boolean check = true;
        try {
            integerToDate(year, month, day);
        }catch (ParseException e){
            check = false;
        }
        return check;
    }
}
