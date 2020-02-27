package com.example.awakedust_merge;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class BootReceiver extends BroadcastReceiver {
    public AlarmManager alarm_manager;
    final String dbname = "Awake";
    final String tableName ="Alarm";
    static final String TAG_Hour="hour";
    static final String TAG_Minute = "minute";
    PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        //스마트폰이 재부팅 되었다면
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Calendar calendarnow = Calendar.getInstance();
            Calendar calendar = (Calendar) calendarnow.clone();
            final Intent my_intent = new Intent(context, Alarm_Receiver.class);

            try{


                SQLiteDatabase ReadDB = context.getApplicationContext().openOrCreateDatabase(dbname,MODE_PRIVATE,null);
                Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
                //ReadDB.execSQL("DROP TABLE " + tableName  );
                if(c!=null){
                    if(c.moveToFirst()){
                        do{
                            int[] weekdays = new int[8];
                            int id = c.getInt(c.getColumnIndex("_id"));
                            String Hour = c.getString(c.getColumnIndex("hour"));
                            String Minute = c.getString(c.getColumnIndex("minute"));
                            int set = c.getInt(c.getColumnIndex("isset"));
                            weekdays[0]=0;
                            weekdays[1] = c.getInt(c.getColumnIndex("sun"));
                            weekdays[2] = c.getInt(c.getColumnIndex("mon"));
                            weekdays[3] = c.getInt(c.getColumnIndex("tue"));
                            weekdays[4] = c.getInt(c.getColumnIndex("wen"));
                            weekdays[5] = c.getInt(c.getColumnIndex("thu"));
                            weekdays[6] = c.getInt(c.getColumnIndex("fri"));
                            weekdays[7] = c.getInt(c.getColumnIndex("sat"));

                            if(set==1) {
                                boolean[] week = new boolean[8];
                                for (int i = 0; i < weekdays.length; i++) {
                                    if (weekdays[i] == 1)
                                        week[i] = true;
                                    else
                                        week[i] = false;
                                }

                                pendingIntent = PendingIntent.getBroadcast(context, id, my_intent,
                                        PendingIntent.FLAG_CANCEL_CURRENT);

                                calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(Hour));
                                calendar.set(Calendar.MINUTE, Integer.valueOf(Minute));
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                if (calendar.compareTo(calendarnow) <= 0) {
                                    calendar.add(Calendar.DATE, 1);
                                }

                                long oneday = 24*60*60*1000;

                                // 알람셋팅
                                boolean repeat=false;
                                for(int i=0;i<week.length;i++){
                                    if(week[i]==true)
                                        repeat = true;
                                    Log.d("이거 트루?",String.valueOf(repeat));
                                }
                                if(repeat==true)
                                    alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),oneday,pendingIntent);
                                else
                                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

                            }

                        } while(c.moveToNext());
                    }
                }
                ReadDB.close();



            } catch (SQLiteException se){
                Toast.makeText(context.getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
}

