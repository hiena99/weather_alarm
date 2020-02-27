package com.example.awakedust_merge;


import android.util.Log;

public class Data_Alarm {

    private String hour;
    private String minute;
    private int set;
    private int id;
    private boolean half;


    public String getHour() {
        if(Integer.valueOf(hour)>12) {
            hour = String.valueOf(Integer.valueOf(hour)-12);
            if(Integer.valueOf(hour)<10)
                return "0"+hour;
            else
                return hour;
        }

        else
            if(Integer.valueOf(hour)<10)
                return "0"+hour;
            else
                return hour;
    }

    public void setHour(String hour) {
        Log.d("isOK",hour);
        this.hour = hour;
    }

    public String getMinute() {
        if(Integer.valueOf(minute)<10)
            return " : 0" + minute;
        else
            return " : "+minute;

    }

    public void setMinute(String minute) {
        Log.d("isOK",minute);
        this.minute = minute;
    }

    public void setAlarm(int set){
        this.set = set;
    }
    public boolean getAlarm(){
        if(set==1)
            return true;
        else
            return false;
    }
    public void setID(int id){
        this.id=id;
    }
    public int getID(){
        return id;
    }

    public void setHalf(boolean half){
        this.half = half;
    }
    public String getHalf(){
        if(half==true)
            return "오후";
        else
            return "오전";
    }


}