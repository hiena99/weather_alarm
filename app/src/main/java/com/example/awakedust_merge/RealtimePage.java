package com.example.awakedust_merge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


public class RealtimePage extends Fragment {
    final int PARSE_STATE_NOT_FOUND = 0;
    final int PARSE_STATE_FOUND = 1;
    final int PARSE_STATE_DONE = 2;
    int x;
    int y;
    double gx;
    double gy;

    final String dbname = "Awake";
    final String tableName ="Alarm";

    ArrayList<HashMap<String,String>> AlarmList;
    static final String TAG_Hour="hour";
    static final String TAG_Minute = "minute";
    private RecyclerAdapter_Alarm adapter2;
    private RecyclerAdapter adapter;
    int count;
    private SharedPreferences sp;
    private static Activity activity;



    ArrayList<Data_Alarm> listData = new ArrayList<>();


    private TextView m_TextViewLog = null;
    private TextView m_Today = null;
    private TextView m_Polution = null;
    private TextView supermise =null;
    private TextView m_TextViewAddress = null;  //등록 위치 주소
    private TextView m_TextViewStationName = null;  //관측소 이름
    private TextView m_TextViewDataTime = null; //오염측정시각
    private TextView m_TextViewPM25 = null; //pm2.5
    private TextView m_TextViewPM25_24 = null; //pm2.5 (24시간)
    private TextView m_TextViewPM10 = null; //pm10
    private TextView m_TextViewPM10_24 = null; //pm10 (24시간)
    private TextView m_temperature = null;
    private linsooLocationMNG llMng = null;
    private OpenAPIQuery openApi = null;
    private GeoPoint in_pt = new GeoPoint(0, 0);
    private GeoPoint tm_pt = new GeoPoint(0, 0);
    GeoTransWeather gw = new GeoTransWeather();


    private XmlPullParserFactory factory= null;
    private XmlPullParser xpp= null;
    private String m_strLogText;

    @Override
    public void onPause(){
        super.onPause();
        llMng.EndFindLocation();
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshData();
        adapter2.notifyDataSetChanged();

    }

    static RealtimePage newInstance(int position) {
        RealtimePage f = new RealtimePage();	//객체 생성
        Bundle args = new Bundle();					//해당 fragment에서 사용될 정보 담을 번들 객체
        args.putInt("position", position);				//포지션 값을 저장
        f.setArguments(args);							//fragment에 정보 전달.
        return f;											//fragment 반환
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context;

        try {
            factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }






        llMng = new linsooLocationMNG(getActivity(), new linsooLocationMNG.resultCallback() {
            @Override
            public void callbackMethod(double latitude, double longitude, String address) {
                Log.d("linsoo","callbackMethod");

                in_pt.x = longitude;    //경도
                in_pt.y = latitude;     //위도
                tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);

                x= (int)tm_pt.x;
                y = (int) tm_pt.y;
                Log.d("locationzz",String.valueOf(longitude));
                Log.d("locationzz",String.valueOf(latitude));

                 GeoTransWeather geoTransWeather = new GeoTransWeather();

                 GeoTransWeather.LatXLngY man =geoTransWeather.convertGRID_GPS(0,latitude,longitude);

                 gx=man.x;
                 gy=man.y;

                Log.d("locationzz",String.valueOf(gx));
                Log.d("locationzz",String.valueOf(gy));


                addLogText(("longitude="+longitude+" latitude="+latitude));

                int textColor = Color.BLACK;
                if(address == null){
                    address = "주소를 구할수 없었 습니 다.";
                    textColor = Color.RED;
                }
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                String getTime = sdf.format(date);
                String[] split_address = address.split(" " );
                Log.d("어디??",address);
                m_TextViewAddress.setText(split_address[2]+" "+split_address[3]);
                String result_address = split_address[2]+" "+split_address[3];
                sp = getActivity().getSharedPreferences("myfile", MODE_PRIVATE);

                SharedPreferences.Editor editor = sp.edit();

                editor.putString("address",result_address);
                editor.commit();



                m_Today.setText(getTime);


                addLogText("공공데이터 포털에서 미세먼지 데이터를 요청합니다");
                openApi.queryGetStationNamefromTM(tm_pt.x, tm_pt.y);




            }
        });




        openApi = new OpenAPIQuery(new OpenAPIQuery.resultCallback() {
           @Override
            public void callbackOpenAPI_GetAirDatafromStationName(String result) {
                Log.d("linsoo","callbackOpenAPI_GetAirDatafromStationName");

                xmlParseGetAirDatafromStationName(result);
            }

            @Override
            public void callbackOpenAPI_Getweathernow(String result) {
                Log.d("linsoo","callbackOpenAPI_GetStationNamefromTM");

                xmlParseGetweathernow(result);
            }

            @Override
            public void callbackOpenAPI_GetStationNamefromTM(String result) {
                Log.d("linsoo","callbackOpenAPI_GetStationNamefromTM");

                xmlParseGetStationNamefromTM(result);
            }


            @Override
            public void callbackOpenAPI_Getweather(String result) {
                Log.d("linsoo","callbackOpenAPI_Getweather");

                xmlParseGetweather(result);
            }

            @Override
            public void callbackOpenAPI_Error(String errReport) {
                Log.d("linsoo", "callbackOpenAPI_Error");

            }
        });
    }



    private void read(){
        try{

            int i=0;
            SQLiteDatabase ReadDB = getActivity().getApplicationContext().openOrCreateDatabase(dbname,MODE_PRIVATE,null);
            Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
            //ReadDB.execSQL("DROP TABLE " + tableName  );
            if(c!=null){
                if(c.moveToFirst()){
                    do{
                        Data_Alarm data1 = new Data_Alarm();
                        boolean half;

                        int id = c.getInt(c.getColumnIndex("_id"));
                        String Hour = c.getString(c.getColumnIndex("hour"));
                        String Minute = c.getString(c.getColumnIndex("minute"));
                        int set = c.getInt(c.getColumnIndex("isset"));

                        if(Integer.valueOf(Hour)>=12)
                            half = true;
                        else
                            half =false;

                        data1.setHalf(half);

                        data1.setID(id);
                        data1.setHour(Hour);
                        data1.setMinute(Minute);
                        data1.setAlarm(set);
                        Log.d("how",String.valueOf(data1.getHour()));
                        Log.d("how",String.valueOf(data1.getMinute()));
                        Log.d("how",String.valueOf(data1.getID()));
                        Log.d("how",String.valueOf(data1.getAlarm()));
                        Log.d("how",String.valueOf(data1));
                        adapter2.addItem(data1);
                        i++;


                    } while(c.moveToNext());
                }
            }
            ReadDB.close();

            adapter2.notifyDataSetChanged();


        } catch (SQLiteException se){
            Toast.makeText(getActivity().getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



//------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    //    m_TextViewLog = (TextView) rootView.findViewById(R.id.textViewLog);
      //  m_TextViewLog.setMovementMethod(new ScrollingMovementMethod());




        m_TextViewAddress =  (TextView) rootView.findViewById(R.id.textAddress);
        m_Today = (TextView) rootView.findViewById(R.id.today);
        m_Polution = (TextView) rootView.findViewById(R.id.mise);
        supermise = (TextView) rootView.findViewById(R.id.chomise);
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        RecyclerView recyclerView_alarm = rootView.findViewById(R.id.recyclerView_alarm);
        ImageButton addButton = rootView.findViewById(R.id.addAlarm);
        addButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){


                //adapter.notifyDataSetChanged();
                Intent timer = new Intent(getActivity().getApplicationContext(),TimerSetting.class);
                // timer.putExtra("number",count);
                startActivity(timer);
                getActivity().finish();



            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
//

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this.getContext());
        recyclerView_alarm.setLayoutManager(linearLayoutManager2);

        adapter2 = new RecyclerAdapter_Alarm(getContext());
        recyclerView_alarm.setAdapter(adapter2);

        read();
        return rootView;
    }



    public void refreshData(){
        Log.d("linsoo", "refreshData");
        clearLogText();
        m_TextViewAddress.setText("");
        try{
            openApi.StopQuery();
            llMng.EndFindLocation();
            llMng.StartFindLocation();
        }catch (Exception e){ Log.e("linsoo", "refreshData="+e.getMessage());      }
    }



    public void xmlParseGetweathernow(String data){
        try{
            String tmpTag;
            String temperTag="";
            ArrayList<String> test= new ArrayList<>();

            String temperature="";
            boolean isok=false;
            boolean temok=false;
            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (tmpTag.equals("obsrValue")) {
                            isok=true;
                        }
                           // temperature=xpp.getText();

                       // Log.d("weather",xpp.getText());
                        break;
                    case XmlPullParser.TEXT:
                        if(isok==true){
                            temperature=xpp.getText();
                            Log.d("where??",temperature);
                            test.add(temperature);
                            isok=false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xpp.next();
            }
            Log.d("plz..",test.get(3));
            m_temperature.setText(test.get(3));


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void xmlParseGetAirDatafromStationName(String data){
        try{
            String tmpTag;
            String temperTag="";
            ArrayList<String> test= new ArrayList<>();
            sp =getActivity().getSharedPreferences("myfile",MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();


            String polution="";
            String superpolution="";
            boolean isok=false;
            boolean isok2 =false;
            boolean temok=false;
            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (tmpTag.equals("pm10Grade")) {
                            isok=true;
                        }
                        if(tmpTag.equals("pm25Grade")){
                            isok2=true;
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
                            edit.putString("mise",polution);
                            edit.commit();
                            test.add(polution);
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
                            isok2=false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xpp.next();
            }
            Log.d("plz..",String.valueOf(test));
            m_Polution.setText(polution);
            supermise.setText(superpolution);


        }
        catch (Exception e){
            e.printStackTrace();
        }
        openApi.queryGetWeatherTemperature(gx, gy);



    }



    public void xmlParseGetweather(String data){
        try{
            String tmpTag;
            String temperTag="";
            ArrayList<String> cate= new ArrayList<>();
            ArrayList<String> val = new ArrayList<>();
            ArrayList<String> ti = new ArrayList<>();

            ArrayList<String> temperature = new ArrayList<>();
            ArrayList<String> sky = new ArrayList<>();
            ArrayList<String> pty = new ArrayList<>();
            ArrayList<String> timer = new ArrayList<>();


            String category="";
            String value="";
            String time="";

            boolean isok1=false;
            boolean isok2=false;
            boolean isok3=false;
            boolean temok=false;
            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (tmpTag.equals("category")) {
                            isok1=true;
                        }

                        else if(tmpTag.equals("fcstTime")){
                            isok2=true;
                        }

                        else if(tmpTag.equals("fcstValue")){
                            isok3=true;
                        }
                        // temperature=xpp.getText();

                        // Log.d("weather",xpp.getText());
                        break;
                    case XmlPullParser.TEXT:
                        if(isok1==true){
                            category=xpp.getText();
                            Log.d("where??",category);
                            cate.add(category);
                            isok1=false;
                        }
                        else if(isok2){
                            time=xpp.getText();
                            Log.d("Where??","time "+time);
                            ti.add(time);
                            isok2=false;
                        }
                        else if(isok3){
                            value=xpp.getText();
                            Log.d("Where??","time "+value);
                            val.add(value);
                            isok3=false;

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
                if(cate.get(i).equals("T3H")){
                    String _val = String.format(val.get(i)+"\u00B0");
                    temperature.add(_val);
                    String _timer = String.format(ti.get(i).substring(0,2)+"시");
                    timer.add(_timer);

                }

                if(cate.get(i).equals("SKY")){
                    sky.add(val.get(i));
                }
                if(cate.get(i).equals("PTY")){
                    Log.d("rain?",val.get(i));
                    pty.add(val.get(i));
                }
            }
            for(int i=0;i<temperature.size();i++)
                Log.d("plz..","zz : "+timer.get(i));

            Log.d("plz..",cate.get(1));
            Log.d("plz..",ti.get(1));
            Log.d("plz..",val.get(1));
            for (int i = 0; i < temperature.size(); i++) {
                // 각 List의 값들을 data 객체에 set 해줍니다.
                Data data1 = new Data();
                data1.setWhattime(timer.get(i));
                if(pty.get(i).equals("0"))
                    data1.setWhatcloud(sky.get(i));
                else
                    data1.setWhatcloud(String.valueOf(Integer.valueOf(pty.get(i))+5));
                data1.setWhattemper(temperature.get(i));


                // 각 값이 들어간 data를 adapter에 추가합니다.
                adapter.addItem(data1);
            }

            // adapter의 값이 변경되었다는 것을 알려줍니다.
            adapter.notifyDataSetChanged();
           // m_temperature.setText(val.get(1));


        }
        catch (Exception e){
            e.printStackTrace();
        }
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

    private void clearLogText(){
        m_strLogText = "";

    }
    private void addLogText(String txt){

    }



    private String changeHtmlToPlainTxt(String html){
        String tmp = html.replace("<", "&lt;");
        return tmp;
    }

}
