package com.example.awakedust_merge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Alarm_Receiver extends BroadcastReceiver {

    public static final String PERMISSION_REPEAT = "com.rightline.backgroundrepeatapp.permission.ACTION_REPEAT";
    public static final String REPEAT_START = "com.rightline.backgroundrepeatapp.REPEAT_START";
    public static final String REPEAT_STOP = "com.rightline.backgroundrepeatapp.REPEAT_STOP";
    Context context;
    final String dbname = "Awake";
    AlarmManager alarm_manager;
    PendingIntent pendingIntent;
    //final Intent my_intent = new Intent(t, Alarm_Receiver.class);


    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);




    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("isOk","알람리시버들어가?");
        boolean[] week = intent.getBooleanArrayExtra("week");
        String uri1 = intent.getStringExtra("alarm_uri");
        Log.d("isOk",uri1);
        int id = intent.getIntExtra("alarmID",0);

        Intent schedule = new Intent(REPEAT_START);
        schedule.setClass(context, Alarm_Receiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, schedule, 0);


        Log.d("weekday",String.valueOf(week[0]));
        Log.d("weekday",String.valueOf(week[1]));
        Log.d("weekday",String.valueOf(week[2]));
        Log.d("weekday",String.valueOf(week[3]));
        Log.d("weekday",String.valueOf(week[4]));
        Log.d("weekday",String.valueOf(week[5]));
        Log.d("weekday",String.valueOf(week[6]));
        Log.d("weekday",String.valueOf(week[7]));
        Calendar calendar = Calendar.getInstance();
        Log.d("울리는 시간",String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        boolean repeat = false;
        Intent intent1 = new Intent(context,AlarmActivity.class);
//        pendingIntent = PendingIntent.getBroadcast(context, id, my_intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        for(int i=0;i<week.length;i++){
            if(week[i]==true)
                repeat = true;
        }

        if(repeat==true){
            if (!week[calendar.get(Calendar.DAY_OF_WEEK)]) {
                calendar.add(Calendar.DATE,1);
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            }

        }
        try {
            Log.d("켜지는경우",String.valueOf(calendar.get((Calendar.DAY_OF_WEEK))));
            intent1.putExtra("alarmID",id);
            intent1.putExtra("week",week);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_ONE_SHOT);
            pi.send();
            this.context = context;
            // intent로부터 전달받은 string
            String get_yout_string = intent.getExtras().getString("state");

            // RingtonePlayingService 서비스 intent 생성
            Intent service_intent = new Intent(context, RingtonePlayingService.class);

            // RingtonePlayinService로 extra string값 보내기
            service_intent.putExtra("state", get_yout_string);
            service_intent.putExtra("alarm_uri",uri1);
            Log.d("알람uri",uri1);

            // start the ringtone service

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                this.context.startForegroundService(service_intent);
                Log.d("isOk","얘는 돼야지");

            }else{
                this.context.startService(service_intent);
                Log.d("isOk","???!!");

            }
        }catch (PendingIntent.CanceledException e){
            e.printStackTrace();
        }


    }
}
