package com.example.awakedust_merge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AlarmActivity extends AppCompatActivity {

    double gx;
    double gy;

    final int PARSE_STATE_NOT_FOUND = 0;
    final int PARSE_STATE_FOUND = 1;
    final int PARSE_STATE_DONE = 2;
    int x;
    int y;

    private OpenAPIQuery openApi = null;
    private linsooLocationMNG llMng = null;
    GeoTransWeather gw = new GeoTransWeather();
    private XmlPullParserFactory factory= null;
    private XmlPullParser xpp= null;
    private ImageView image=null;
    private TextView temper=null;
    private TextView textweather = null;
    private TextView textmise=null;
    private TextView textchomise=null;
    private TextView numbermise =null;
    private TextView numberchomise=null;
    private GeoPoint in_pt = new GeoPoint(0, 0);
    private GeoPoint tm_pt = new GeoPoint(0, 0);
    final String dbname = "Awake";

    @Override
    public void onPause(){
        super.onPause();
        llMng.EndFindLocation();
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshData();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        final Intent intent = new Intent(this,RingtonePlayingService.class);
        final TextView Adress = (TextView) findViewById(R.id.address);
        final SharedPreferences sp = getSharedPreferences("myfile",MODE_PRIVATE);
        Intent getintent = getIntent();
        int alarmid =getintent.getIntExtra("alarmID",0);
        boolean[] week = getintent.getBooleanArrayExtra("week");
        boolean repeat =false;

        for(int i=0; i<week.length; i++){
            if(week[i])
                repeat=true;
        }

        if(repeat==false) {
            try {
                SQLiteDatabase ReadDB = this.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
                //Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
                String update = "UPDATE Alarm SET isset=" + String.valueOf(0) + " WHERE _id=" + String.valueOf(alarmid);
                ReadDB.execSQL(update);
            } catch (Exception e) {
            }
        }
        try {
            factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }





        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("hh : mm");
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy-MM-dd E요일");
        final MediaPlayer mediaPlayer = new MediaPlayer();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String _hour="";
        int minute = calendar.get(Calendar.MINUTE);
        String _minute="";
        String half="";

        String today =format2.format(calendar.getTime());


        if(hour>=12) {
            _hour = String.valueOf(hour - 12);
            half="오후";
        }
        else
            half="오전";
        if(hour<10)
            _hour="0"+String.valueOf(hour);

        if(minute<10)
            _minute="0"+String.valueOf(minute);

        String now = _hour + " : " + _minute;
        now = format1.format(calendar.getTime());
        Data_location location = new Data_location();

       /* String address = sp.getString("address","안되네");
        Log.d("djeldi",address);

        Adress.setText(address);*/



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Button button = (Button) findViewById(R.id.stopbutton);
        TextView _now = (TextView) findViewById(R.id.timerightnow);
        TextView _ampm=(TextView) findViewById(R.id.ampm);
        TextView _today =(TextView)findViewById(R.id.todaydate);

        _ampm.setText(half);
        _now.setText(now);
        _today.setText(today);

//        llMng.EndFindLocation();
//        llMng.StartFindLocation();

        llMng = new linsooLocationMNG(this, new linsooLocationMNG.resultCallback() {
            @Override
            public void callbackMethod(double latitude, double longitude, String address) {
                Log.d("flstm","callbackMethod");


                Log.d("locationzz",String.valueOf(longitude));
                Log.d("locationzz",String.valueOf(latitude));

                in_pt.x = longitude;    //경도
                in_pt.y = latitude;     //위도
                tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);

                x= (int)tm_pt.x;
                y = (int) tm_pt.y;

                GeoTransWeather geoTransWeather = new GeoTransWeather();

                GeoTransWeather.LatXLngY man =geoTransWeather.convertGRID_GPS(0,latitude,longitude);

                gx=man.x;
                gy=man.y;
                if(address == null){
                    address = "? ? 새로고침 해주세요. ";

                }
                String[] split_address = address.split(" " );
                String result_address = split_address[2]+" "+split_address[3];
                Adress.setText(result_address);


                Log.d("zzlocationzz",String.valueOf(gx));
                Log.d("zzlocationzz",String.valueOf(gy));

                openApi.queryGetStationNamefromTM(x,y);
            }
        });



        openApi = new OpenAPIQuery(new OpenAPIQuery.resultCallback() {
            @Override
            public void callbackOpenAPI_GetAirDatafromStationName(String result) {
                Log.d("durl?","fdsfef");
                xmlParseGetAirDatafromStationName(result);
            }

            @Override
            public void callbackOpenAPI_GetStationNamefromTM(String result) {
                xmlParseGetStationNamefromTM(result);
            }

            @Override
            public void callbackOpenAPI_Getweather(String result) {

            }

            @Override
            public void callbackOpenAPI_Error(String errReport) {

            }

            @Override
            public void callbackOpenAPI_Getweathernow(String result) {
                Log.d("dlWhr??","콜백은 들어가느냐");
                xmlParseGetweathernow(result);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("state","alarm off");
                Log.d("where??" ,String.valueOf(intent));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    startForegroundService(intent);
                    Log.d("isOk","얘는 돼야지");

                }else{
                    startService(intent);
                    Log.d("isOk","???!!");

                }

                sendBroadcast(intent);
                //mediaPlayer.stop();
                //mediaPlayer.release();
            }
        });


    }

    public void refreshData(){
        Log.d("linsoo", "refreshData");
        try{
            openApi.StopQuery();
            llMng.EndFindLocation();
            llMng.StartFindLocation();
        }catch (Exception e){ Log.e("linsoo", "refreshData="+e.getMessage());      }
    }



    public void xmlParseGetStationNamefromTM(String data) {
        try {
            Log.d("there..","mm");
            String tmpTag;
            int foundStationName = PARSE_STATE_NOT_FOUND;

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (foundStationName == PARSE_STATE_NOT_FOUND && tmpTag.equals("stationName"))
                            foundStationName = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(foundStationName == PARSE_STATE_FOUND){
                            foundStationName = PARSE_STATE_DONE;
                            //m_TextViewStationName.setText(xpp.getText());
                            openApi.queryGetAirDatafromStationName(xpp.getText());

                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void xmlParseGetAirDatafromStationName(String data){
        try{
            String tmpTag;
            String temperTag="";
            ArrayList<String> test= new ArrayList<>();

            textmise=(TextView) findViewById(R.id.textmise);
            textchomise=(TextView) findViewById(R.id.textchomise);
            numbermise=(TextView) findViewById(R.id.numbermise);
            numberchomise=(TextView) findViewById(R.id.numberchomise);



            String polution="";
            String superpolution="";
            String number1 ="";
            String number2="";
            boolean isok=false;
            boolean isok2=false;
            boolean isok3=false;
            boolean isok4=false;
            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (tmpTag.equals("pm25Grade")) {
                            isok=true;
                        }
                        if(tmpTag.equals("pm10Grade")){
                            isok2=true;
                        }
                        if(tmpTag.equals("pm10Value")){
                            isok3=true;
                        }
                        if(tmpTag.equals("pm25Value")){
                            isok4=true;
                        }
                        // temperature=xpp.getText();

                        // Log.d("weather",xpp.getText());
                        break;
                    case XmlPullParser.TEXT:
                        if(isok==true){
                            polution=xpp.getText();
                            Log.d("where??",polution);
                            if(polution.equals("1"))
                                polution="좋음";
                            else if(polution.equals("2"))
                                polution="보통";
                            else if(polution.equals("3"))
                                polution="나쁨";
                            else
                                polution="매우 나쁨";
                            test.add(polution);
                            textchomise.setText(polution);
                            isok=false;
                        }
                        if(isok2==true){
                            superpolution=xpp.getText();
                            Log.d("where??",superpolution);
                            if(superpolution.equals("1"))
                                superpolution="좋음";
                            else if(superpolution.equals("2"))
                                superpolution="보통";
                            else if(superpolution.equals("3"))
                                superpolution="나쁨";
                            else
                                superpolution="매우 나쁨";
                            test.add(superpolution);
                            textmise.setText(superpolution);
                            isok2=false;
                        }
                        if(isok3==true){
                            number1=xpp.getText();
                            numbermise.setText(number1);
                            isok3=false;
                        }
                        if(isok4==true){
                            number2=xpp.getText();
                            numberchomise.setText(number2);
                            isok4=false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xpp.next();
            }
            Log.d("plz..",String.valueOf(test));
          //  m_Polution.setText(polution);


        }
        catch (Exception e){
            e.printStackTrace();
        }
        openApi.queryGetWeatherTemperature_now(gx, gy);



    }

    @Override
    public void onBackPressed(){

        Intent intent = new Intent(this,RingtonePlayingService.class);

        intent.putExtra("state","alarm off");
        Log.d("where??" ,String.valueOf(intent));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            startForegroundService(intent);
            Log.d("isOk","얘는 돼야지");

        }else{
            startService(intent);
            Log.d("isOk","???!!");

        }

        sendBroadcast(intent);
        Intent back = new Intent(AlarmActivity.this,MainActivity.class);
        startActivity(back);
        finish();

    }



    public void xmlParseGetweathernow(String data){
        try{
            String tmpTag;

            ArrayList<String> cate= new ArrayList<>();
            ArrayList<String> val = new ArrayList<>();
            ArrayList<String> temperature = new ArrayList<>();
            ArrayList<String> sky = new ArrayList<>();
            ArrayList<String> pty = new ArrayList<>();

            String _sky="";
            String _pty="";



            String category="";
            String value="";


            boolean isok1=false;
            boolean isok2=false;

            image = (ImageView) findViewById(R.id.weather);
            temper = (TextView) findViewById(R.id.temper);
            textweather = (TextView) findViewById(R.id.textweather);

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (tmpTag.equals("category")) {
                            isok1=true;
                        }

                        else if(tmpTag.equals("fcstValue")){
                            isok2=true;
                        }

                        // temperature=xpp.getText();

                        // Log.d("weather",xpp.getText());
                        break;
                    case XmlPullParser.TEXT:
                        if(isok1==true){
                            category=xpp.getText();
                            Log.d("dlWhr??",category);
                            cate.add(category);
                            isok1=false;
                        }
                        else if(isok2){
                            value=xpp.getText();
                            Log.d("dlWhr??","value "+value);
                            val.add(value);
                            isok2=false;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        //  Log.d("where??",test.get(3));
                        //m_temperature.setText(test.get(3));
                        break;
                }
                eventType = xpp.next();
            }
            for(int i=0;i<val.size();i++){
                if(cate.get(i).equals("T1H")){
                    String _val = String.format(val.get(i)+"\u00B0");
                    Log.d("zzwpqkf",_val);

                    temper.setText(_val);
                }

                if(cate.get(i).equals("SKY")){
                    Log.d("zzsky?",val.get(i));
                    sky.add(val.get(i));
                    _sky=val.get(i);
                }
                if(cate.get(i).equals("PTY")){
                    Log.d("zzrain?",val.get(i));
                    pty.add(val.get(i));
                    _pty=val.get(i);
                }

            }

            if(_pty.equals("0")){
                if(Integer.valueOf(_sky)==1) {
                    image.setImageResource(R.drawable.sunny);
                    textweather.setText("맑음");
                }
                else if(Integer.valueOf(_sky)==3) {
                    image.setImageResource(R.drawable.cloudy);
                    textweather.setText("구름 많음");
                }
                else if(Integer.valueOf(_sky)==4) {
                    image.setImageResource(R.drawable.morecloudy);
                    textweather.setText("흐림");
                }
            }
            else{
                if(Integer.valueOf(_pty)==1) {
                    image.setImageResource(R.drawable.rainy);
                    textweather.setText("비");
                }
                else if(Integer.valueOf(_pty)==3) {
                    image.setImageResource(R.drawable.snowy);
                    textweather.setText("눈");
                }

            }



            Log.d("dlWhrplz..",cate.get(1));
            Log.d("dlWhrplz..",val.get(1));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
      }
}
