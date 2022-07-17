package com.google.codelabs.mdc.java.shrine.utils;

import android.content.Context;
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

    public static void switchActivity(Context currentActivity, Class<?> nextActivity){
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

//    /**
//     * hàm kiểm tra xem đã mở khoá ở device chưa và response từ api call rent bike thành công không
//     *
//     * @param context context hiện tại
//     * @return true nếu đã mở khoá + response call api là thành công
//     */
//    public static boolean checkRentBikeSuccess(Context context){
//        MyStorage myStorage = new MyStorage(context);
//        String openLockSuccess = myStorage.get(Constant.OPEN_LOCK_SUCCESS_KEY);
//        String contractBike = myStorage.get(Constant.CONTRACT_BIKE_KEY);
//        return !"".equals(openLockSuccess) && !"".equals(contractBike);
//    }

    /**
     * convert date về dạng 19h:20p ngày 19/3/2022
     * @param date date cần convert
     * @return date dạng string sau khi đã convert
     */
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH'h:'mm'p ngày 'dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }


}
