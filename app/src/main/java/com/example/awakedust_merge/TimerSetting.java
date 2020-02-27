package com.example.awakedust_merge;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TimerSetting extends AppCompatActivity {

    final String dbname = "Awake";
    final String tableName ="Alarm";
    public static final int REQUEST_CODE_RINGTONE = 10005;

    ArrayList<HashMap<String,String>> AlarmList;
    static final String TAG_Hour="hour";
    static final String TAG_Minute = "minute";
    static final String TAG_id = "_id";

   

    SQLiteDatabase sampleDB = null;
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    private RecyclerAdapter_Alarm adapter=new RecyclerAdapter_Alarm(this);
    PendingIntent pendingIntent;
    int hour;
    int minute;
    Data data = new Data();
    private TextView textView;
    private Button back;
    String alarmname;
    String merong="기본 알람";

    private ToggleButton _toggleSun, _toggleMon, _toggleTue, _toggleWed, _toggleThu, _toggleFri, _toggleSat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesetting);

        //Log.d("hungry",test);

        _toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        _toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        _toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        _toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        _toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        _toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        _toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);

         textView = (TextView) findViewById(R.id.alarmname);





        this.context = this;

        // 알람매니저 설정
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 타임피커 설정
        alarm_timepicker = findViewById(R.id.time_picker);

        // Calendar 객체 생성
        final Calendar calendarnow = Calendar.getInstance();
        Log.d("시간",String.valueOf(calendarnow));
        Log.d("시간",String.valueOf(calendarnow.get(Calendar.DAY_OF_WEEK)));
        final Calendar calendar = (Calendar) calendarnow.clone();




        // 알람리시버 intent 생성
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);

        // 알람 시작 버튼
        Button alarm_on = findViewById(R.id.btn_start);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                boolean[] week = { false, _toggleSun.isChecked(), _toggleMon.isChecked(), _toggleTue.isChecked(), _toggleWed.isChecked(),
                        _toggleThu.isChecked(), _toggleFri.isChecked(), _toggleSat.isChecked() };

                // calendar에 시간 셋팅
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if(calendar.compareTo(calendarnow)<=0){
                    calendar.add(Calendar.DATE,1);
                }
                // 시간 가져옴
                hour = alarm_timepicker.getHour();
                minute = alarm_timepicker.getMinute();

                SQLiteInsert();
                int alarmID = SQLID();
                my_intent.putExtra("state", "alarm on");
                my_intent.putExtra("week",week);
                my_intent.putExtra("alarmID",alarmID);
                Log.d("알람merong", merong);
                my_intent.putExtra("alarm_uri",merong);


                pendingIntent = PendingIntent.getBroadcast(TimerSetting.this, alarmID, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                long oneday = 24*60*60*1000;

                // 알람셋팅
                boolean repeat=false;
                for(int i=0;i<week.length;i++){
                    if(week[i]==true)
                        repeat = true;
                    Log.d("이거 트루?",String.valueOf(repeat));
                }
//                if(repeat==true)
//                    alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),oneday,pendingIntent);
//                else
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);

                Intent test = new Intent(TimerSetting.this, MainActivity.class);;
                startActivity(test);
                finish();

            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gomain = new Intent(TimerSetting.this,MainActivity.class);
                startActivity(gomain);
                finish();

            }
        });




        //음악선택 버튼
        Button select = findViewById(R.id.btn_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); // 암시적 Intent
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람 벨소리를  선택하세요");  // 제목을 넣는다.
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);  // 무음을 선택 리스트에서 제외
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true); // 기본 벨소리는 선택 리스트에 넣는다.
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
                startActivityForResult(intent, REQUEST_CODE_RINGTONE); // 벨소리 선택 창을 안드로이드OS에 요청
            }
        });




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case REQUEST_CODE_RINGTONE :
                if(resultCode == RESULT_OK ) {
                    // 선택한 Ringtone(벨소리)를 받아온다.
                    Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    // 벨소리 이름 얻는 방법
                    String ringToneName = RingtoneManager.getRingtone(this, uri).getTitle(this);
                    textView.setText(ringToneName);
                    merong = String.valueOf(uri);



                }
        }
    }

    @Override
    public void onBackPressed(){
        Intent back = new Intent(TimerSetting.this,MainActivity.class);
        startActivity(back);
        finish();
    }





    private void SQLiteInsert(){
            AlarmList = new ArrayList<HashMap<String, String>>();

            try {

                String stringhour = String.valueOf(hour);

                String stringminute = String.valueOf(minute);

                String link = merong;
                int sun, mon, tue, wen, thu, fri,sat;
                if(_toggleSun.isChecked())
                    sun=1;
                else
                    sun =0;
                if(_toggleMon.isChecked())
                    mon=1;
                else
                    mon =0;
                if(_toggleTue.isChecked())
                    tue=1;
                else
                    tue =0;
                if(_toggleWed.isChecked())
                    wen=1;
                else
                    wen =0;
                if(_toggleThu.isChecked())
                    thu=1;
                else
                    thu =0;
                if(_toggleFri.isChecked())
                    fri=1;
                else
                    fri =0;
                if(_toggleSat.isChecked())
                    sat=1;
                else
                    sat =0;
                int isset = 1;
                sampleDB = this.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
                //sampleDB.execSQL("DROP TABLE " + tableName  );
                sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (_id integer PRIMARY KEY autoincrement, hour VARCHAR(20), minute VARCHAR(20), isset integer, sun integer, mon integer, tue integer, wen integer, thu integer, fri integer, sat integer, uri VARCHAR(100));");
                //sampleDB.execSQL("DELETE FROM " + tableName  );



                sampleDB.execSQL("INSERT INTO " + tableName +" (hour, minute,isset,sun,mon,tue,wen,thu,fri,sat,uri) Values('" + stringhour + "','" + stringminute + "',1,'"+sun+"','"+mon+"','"+tue+"','"+wen+"','"+thu+"','"+fri+"','"+sat+"','"+link+"');");

                //sampleDB.close();
            }

            catch (SQLiteException se) {
                Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
            }

    }

    private int SQLID() {
        int id=0;
        SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
        Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + TAG_id + " DESC", null);
        if (c != null) {
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex("_id"));
            }
        }
        Log.d("returnzz",String.valueOf(id));
        return id;

    }

    AlarmManagerUtil.setOnceAlarm(hourOfDay, minute, getRepeatingAlarmPendingIntent(dayInt))
    private void SQLDelete(String modi){

        Log.d("idonknow",modi);
        try {
            SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
            //sampleDB.execSQL("DROP TABLE " + tableName  );
            ReadDB.execSQL("DELETE FROM " + tableName + " WHERE _id="+modi+";");
            //sampleDB.execSQL("DELETE FROM " + tableName  );

        }

        catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
        }

        if(pendingIntent!=null){
            alarm_manager.cancel(pendingIntent);
            pendingIntent.cancel();
        }


    }
}