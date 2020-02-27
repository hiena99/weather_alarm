package com.example.awakedust_merge;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

public class OpenAPIQuery {
    private final String strServiceKey = "D52qWXciCPOPcnAqLGatrty1RvEA6TFHQjDKHPwV12LnJDRonDX9opXN1ZjnoJrtCc%2FRC%2BDAXJO0NwjE3vTDXw%3D%3D";


    private final int QueryTypeNONE = 0;
    private final int QueryTypeGetStationNamefromTM = 1;
    private final int QueryTypeGetAirDatafromStationName = 2;
    private final int QueryTypeGetWeatherTemperature = 3;  ///
    private final int QueryTypeGetWeatherTemperature_now = 4;  ///

    private int m_iQueryType =QueryTypeNONE;
    private resultCallback m_callback = null;

    private OpenAPIThreadTask mThreadAPI = null;


    interface resultCallback { // 인터페이스는 외부에 구현해도 상관 없습니다.
        void callbackOpenAPI_GetAirDatafromStationName(String result);
        void callbackOpenAPI_GetStationNamefromTM(String result);
        void callbackOpenAPI_Getweather(String result);
        void callbackOpenAPI_Error(String errReport);
        void callbackOpenAPI_Getweathernow(String result);
    }

    public OpenAPIQuery( resultCallback callback){
        m_callback = callback;
    }

    public void StopQuery(){
        Log.d("linsoo", "queryThreadStop");
        if(mThreadAPI !=null){
            mThreadAPI.cancel(true);
            mThreadAPI = null;
        }
    }

    public  void queryGetStationNamefromTM(double tmX, double tmY){
        Log.d("linsoo", "queryGetStationNamefromTM");
        try{
            m_iQueryType = QueryTypeGetStationNamefromTM;
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
            Log.d("plz..","station : "+Double.toString(tmX));

            urlBuilder.append("?" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode(Double.toString(tmX), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode(Double.toString(tmY), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);

            if(mThreadAPI != null)
                StopQuery();
            mThreadAPI = new OpenAPIThreadTask();
            mThreadAPI.execute(urlBuilder.toString(),null,null);
        }catch (Exception e){ Log.e("linsoo", "queryGetStationNamefromTM="+e.getMessage());}


    }

    public  void queryGetAirDatafromStationName(String stationName){
        Log.d("linsoo", "queryGetAirDatafromStationName");
        try{
            m_iQueryType = QueryTypeGetAirDatafromStationName;
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");

            urlBuilder.append("?" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode(stationName, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataTerm","UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);
            urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8"));

            if(mThreadAPI != null)
                StopQuery();
            mThreadAPI = new OpenAPIThreadTask();
            mThreadAPI.execute(urlBuilder.toString(),null,null);

        }catch (Exception e){ Log.e("linsoo", "queryGetAirDatafromStationName="+e.getMessage());}
    }





    private Calendar getLastBaseTime(Calendar calBase) {

        int t = calBase.get(Calendar.HOUR_OF_DAY);

        if (t < 2) {

            calBase.add(Calendar.DATE, -1);

            calBase.set(Calendar.HOUR_OF_DAY, 23);

        } else {

            calBase.set(Calendar.HOUR_OF_DAY, t - (t + 1) % 3);

        }

        return calBase;

    }
    //----------시간변환 https://glow153.tistory.com/12?category=759016


    public void queryGetWeatherTemperature(double tmX, double tmY){
        Log.d("linsoo", "queryGetWeatherTemperature");
        try{

            Calendar calendar =Calendar.getInstance();

            int year = getLastBaseTime(calendar).get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int date = getLastBaseTime(calendar).get(Calendar.DATE);
            int hour = getLastBaseTime(calendar).get(Calendar.HOUR_OF_DAY);
            int minute = 0;

            Log.d("omgomg",String.valueOf(year));

            if(hour<5)
                hour=2;
            else if(hour<8)
                hour=2;
            else if(hour<11)
                hour=5;
            else if(hour<14)
                hour=8;
            else if(hour<17)
                hour=11;
            else if(hour<20)
                hour=14;
            else if(hour<23)
                hour=17;
            else
                hour=20;



            //month = calendar.get(Calendar.MONTH);
            //date = calendar.get(Calendar.DATE);
            String _year = String.valueOf(year);
            String _month = String.valueOf(month);
            String _date = String.valueOf(date);
            String _hour = String.valueOf(hour);
            String _minute = String.valueOf(minute);



            if(month<10){
                _month="0"+ String.valueOf(month);
            }

            if(date<10){
                _date="0"+ String.valueOf(date);
            }

            if(hour<10){
                _hour="0"+ String.valueOf(hour);
            }


            if(minute<10){
                _minute="0"+ String.valueOf(minute);
            }

            String today = _year+_month+_date;
            Log.d("omgomg",today);
            String now = _hour+_minute;
            Log.d("omgomg","adsf : "+now);


            m_iQueryType = QueryTypeGetWeatherTemperature;
            Log.d("plz..",String.valueOf(tmX));
            Log.d("plz..",String.valueOf(tmY));
            int x = (int)tmX;
            int y = (int)tmY;

            StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(today, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(now, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(Integer.toString(x), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(Integer.toString(y), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8"));


            if(mThreadAPI != null)
                StopQuery();
            mThreadAPI = new OpenAPIThreadTask();
            mThreadAPI.execute(urlBuilder.toString(),null,null);
        }catch (Exception e){ Log.e("linsoo", "queryGetweather="+e.getMessage());}
    }

    public void queryGetWeatherTemperature_now(double tmX, double tmY){
        Log.d("linsoo", "queryGetWeatherTemperature");
        try{

            Calendar calendar =Calendar.getInstance();

            int year = getLastBaseTime(calendar).get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int date = getLastBaseTime(calendar).get(Calendar.DATE);
            int hour = getLastBaseTime(calendar).get(Calendar.HOUR_OF_DAY);
            int minute = getLastBaseTime(calendar).get(Calendar.MINUTE);

            //month = calendar.get(Calendar.MONTH);
            //date = calendar.get(Calendar.DATE);
            String _year = String.valueOf(year);
            String _month = String.valueOf(month);
            String _date = String.valueOf(date);
            String _hour = String.valueOf(hour);
            String _minute = String.valueOf(minute);

            if(month<10){
                _month="0"+ String.valueOf(month);
            }

            if(date<10){
                _date="0"+ String.valueOf(date);
            }

            if(hour<10){
                _hour="0"+ String.valueOf(hour-1);
            }


            if(minute<10){
                _minute="0"+ String.valueOf(minute);
            }

            if(Integer.valueOf(_minute)>30)
                _minute="30";
            else
                _minute="00";

            String today = _year+_month+_date;
            Log.d("where??",today);
            String now = _hour+_minute;
            Log.d("where??","adsf : "+now);


            m_iQueryType = QueryTypeGetWeatherTemperature_now;
            Log.d("plz..",String.valueOf(tmX));
            Log.d("plz..",String.valueOf(tmY));
            int x = (int)tmX;
            int y = (int)tmY;

            StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastTimeData");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+ strServiceKey);
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(today, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(now, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(Integer.toString(x), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(Integer.toString(y), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8"));


            if(mThreadAPI != null)
                StopQuery();
            mThreadAPI = new OpenAPIThreadTask();
            mThreadAPI.execute(urlBuilder.toString(),null,null);
        }catch (Exception e){ Log.e("linsoo", "queryGetweather="+e.getMessage());}
    }



    private class OpenAPIThreadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urlString) {
            Log.d("linsoo", "doInBackground");
            try{
                URL url = new URL(urlString[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Thread.sleep(100);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();
                return sb.toString();

            }catch (Exception e){
                Log.e("linsoo", "error="+e.getMessage());
                if (m_callback != null){
                    m_callback.callbackOpenAPI_Error(e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("linsoo", "onPostExecute");
            mThreadAPI = null;
            if(result != null) {
                if (m_callback != null){
                    switch (m_iQueryType){
                        case QueryTypeGetStationNamefromTM:  m_callback.callbackOpenAPI_GetStationNamefromTM(result);   break;
                        case QueryTypeGetAirDatafromStationName: m_callback.callbackOpenAPI_GetAirDatafromStationName(result);  break;
                        case QueryTypeGetWeatherTemperature: m_callback.callbackOpenAPI_Getweather(result); break;
                        case QueryTypeGetWeatherTemperature_now: m_callback.callbackOpenAPI_Getweathernow(result); break;
                    }
                }
            }
        }


    }
}
