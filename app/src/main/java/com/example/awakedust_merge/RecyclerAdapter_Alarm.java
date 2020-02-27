package com.example.awakedust_merge;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class RecyclerAdapter_Alarm extends RecyclerView.Adapter<RecyclerAdapter_Alarm.ItemViewHolder> {
    //SQL sql = new SQL();
    Context ctx;
    public RecyclerAdapter_Alarm(Context context){
        this.ctx=context;
    }



    // adapter에 들어갈 list 입니다.
    AlarmManager alarm_manager;
    PendingIntent pendingIntent;
    private ArrayList<Data_Alarm> listData = new ArrayList<>();
    final String dbname = "Awake";
    final String tableName ="Alarm";
    static final String TAG_Hour="hour";
    static final String TAG_Minute = "minute";
    int count = 1;
    private ArrayList<String> idtag = new ArrayList<>();

    ArrayList<Integer> idlist = new ArrayList<>();
    public int goodposition;
    SQLiteDatabase sampleDB = null;


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);

    }



    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Data_Alarm data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
        Log.d("isOK","안에 몇개 ?? "+String.valueOf(listData.size()));
    }




    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView0;
        private TextView textView1;
        private TextView textView2;
        public Switch toggleButton;

        Data_Alarm chkdata = new Data_Alarm();


        //UsingSql sql = new UsingSql();


        ItemViewHolder(View itemView) {

            super(itemView);

          //  sql.getid();
            textView0 = itemView.findViewById(R.id.textView0);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            toggleButton = itemView.findViewById(R.id.toggle);

        }





        void onBind(Data_Alarm data) {
            toggleButton.setOnClickListener(null);
            Log.d("여기가 문제?",String.valueOf(data.getID())+" "+String.valueOf(data.getAlarm()));
            textView0.setText(data.getHalf());
            textView1.setText(data.getHour());
            textView2.setText(data.getMinute());
            toggleButton.setChecked(data.getAlarm());
            toggleButton.setTag(data.getID());
            idtag.add(String.valueOf(data.getID()));


            Log.d("Countzz","count : "+String.valueOf(data.getID()));
            count++;

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        goodposition=position;
        holder.onBind(listData.get(position));
        Log.d("요놈!ㅋㅋ",String.valueOf(position));
        final Data_Alarm good = listData.get(position);
        Data_Alarm data = new Data_Alarm();
        //idtag.add(String.valueOf(data.getID()));
        Log.d("hungry",String.valueOf(idtag));
        holder.itemView.setOnClickListener(null);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////////////////////////
                Context context = view.getContext();
                alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Calendar calendarnow = Calendar.getInstance();
                Calendar calendar = (Calendar) calendarnow.clone();

                String Hour="";
                String Minute="";
                boolean[] week = new boolean[8];
                String uri = null;
                try {
                    SQLiteDatabase ReadDB = context.openOrCreateDatabase(dbname, MODE_PRIVATE, null);
                    //Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
                  //  String update = "UPDATE Alarm SET isset=" + String.valueOf(1) + " WHERE _id=" + String.valueOf(idtag.get(position));
                    //ReadDB.execSQL(update);


                    String id = String.valueOf(idtag.get(position));
                    int alarmID = Integer.valueOf(id);
                    Log.d("isOO", String.valueOf(alarmID));
                    int[] weekdays = new int[8];




                    Cursor c = ReadDB.rawQuery("SELECT * FROM " + tableName + " WHERE _id =" + id, null);
                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                weekdays[0]=0;
                                weekdays[1] = c.getInt(c.getColumnIndex("sun"));
                                weekdays[2] = c.getInt(c.getColumnIndex("mon"));
                                weekdays[3] = c.getInt(c.getColumnIndex("tue"));
                                weekdays[4] = c.getInt(c.getColumnIndex("wen"));
                                weekdays[5] = c.getInt(c.getColumnIndex("thu"));
                                weekdays[6] = c.getInt(c.getColumnIndex("fri"));
                                weekdays[7] = c.getInt(c.getColumnIndex("sat"));
                                Hour = c.getString(c.getColumnIndex("hour"));
                                Minute = c.getString(c.getColumnIndex("minute"));
                                uri = c.getString(c.getColumnIndex("uri"));
                            } while (c.moveToNext());
                        }
                    }

                    for (int i = 0; i < weekdays.length; i++) {
                        if (weekdays[i] == 1)
                            week[i] = true;
                        else
                            week[i] = false;
                    }
                    //final Intent my_intent = new Intent(context, Alarm_Receiver.class);

                    //my_intent.putExtra("week", week);
                    //my_intent.putExtra("state", "alarm on");
                    // my_intent.putExtra("alarm_uri", uri);
                }catch (Exception e){}
                //////////////////////////////////////

                Log.d("hungry2",idtag.get(position));
                Intent intent = new Intent(context,timesetting_modify.class);
                intent.putExtra("modify",idtag.get(position));
                intent.putExtra("week",week);
                intent.putExtra("alarm_uri",uri);
                intent.putExtra("hour",Hour);
                intent.putExtra("minute",Minute);
                context.startActivity(intent);
                ((Activity) context).finish();


            }
        });

        holder.toggleButton.setOnClickListener(null);
        holder.toggleButton.setChecked(good.getAlarm());
        holder.toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(((Switch) view).isChecked()){

                    Context context = view.getContext();
                    alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    Calendar calendarnow = Calendar.getInstance();
                    Calendar calendar = (Calendar) calendarnow.clone();

                    String Hour="";
                    String Minute="";
                    try{
                        SQLiteDatabase ReadDB = context.openOrCreateDatabase(dbname,MODE_PRIVATE,null);
                        //Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
                        String update = "UPDATE Alarm SET isset="+String.valueOf(1)+" WHERE _id="+String.valueOf(view.getTag());
                        ReadDB.execSQL(update);



                        String id=String.valueOf(((Switch) view).getTag());
                        int alarmID = Integer.valueOf(id);
                        Log.d("isOO", String.valueOf(alarmID));
                        int [] weekdays = new int[8];
                        boolean[] week = new boolean[8];
                        String uri=null;
                        Log.d("요놈!",String.valueOf(alarmID));
                        week[0]=false;


                        Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" WHERE _id ="+id,null);
                        if(c!=null) {
                            if (c.moveToFirst()) {
                                do {
                                    weekdays[0]=0;
                                    weekdays[1]=c.getInt(c.getColumnIndex("sun"));
                                    weekdays[2]=c.getInt(c.getColumnIndex("mon"));
                                    weekdays[3]=c.getInt(c.getColumnIndex("tue"));
                                    weekdays[4]=c.getInt(c.getColumnIndex("wen"));
                                    weekdays[5]=c.getInt(c.getColumnIndex("thu"));
                                    weekdays[6]=c.getInt(c.getColumnIndex("fri"));
                                    weekdays[7]=c.getInt(c.getColumnIndex("sat"));
                                    Hour = c.getString(c.getColumnIndex("hour"));
                                    Minute = c.getString(c.getColumnIndex("minute"));
                                    uri = c.getString(c.getColumnIndex("uri"));
                                } while (c.moveToNext());
                            }
                        }

                        boolean isWeek=false;
                        int oneday = 60*60*24*1000;
                        for(int i =0 ; i <weekdays.length;i++){
                            if(weekdays[i]==1) {
                                week[i] = true;
                                isWeek=true;
                            }

                            else
                                week[i]=false;
                        }
                        final Intent my_intent = new Intent(context, Alarm_Receiver.class);

                        my_intent.putExtra("week",week);
                        my_intent.putExtra("state", "alarm on");
                        my_intent.putExtra("alarm_uri",uri);

                        pendingIntent = PendingIntent.getBroadcast(context, alarmID, my_intent,
                                PendingIntent.FLAG_CANCEL_CURRENT);

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(Hour));
                        calendar.set(Calendar.MINUTE, Integer.valueOf(Minute));
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        if(calendar.compareTo(calendarnow)<=0){
                            calendar.add(Calendar.DATE,1);
                        }

                        if(!isWeek) {
                            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                    pendingIntent);
                        }
                        else{
                            alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),oneday,pendingIntent);
                        }



                    } catch (SQLiteException se){
                        Toast.makeText(context.getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    good.setAlarm(1);
                 //   ((Switch) view).setChecked(true);
                    notifyDataSetChanged();
                }

                else{
                    Context context = view.getContext();
                    final Intent my_intent = new Intent(context, Alarm_Receiver.class);
                    AlarmManager alarm_manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                    try{
                        SQLiteDatabase ReadDB = context.openOrCreateDatabase(dbname,MODE_PRIVATE,null);
                        //Cursor c =ReadDB.rawQuery("SELECT * FROM "+tableName+" ORDER BY "+TAG_Hour +" ASC"+", "+TAG_Minute+" ASC",null);
                        String update = "UPDATE Alarm SET isset="+String.valueOf(0)+" WHERE _id="+String.valueOf(view.getTag());
                        ReadDB.execSQL(update);

                        String id=String.valueOf(((Switch) view).getTag());
                        int alarmID = Integer.valueOf(id);
                        Log.d("isOO", String.valueOf(alarmID));

                        pendingIntent = PendingIntent.getBroadcast(context, alarmID, my_intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        if(pendingIntent!=null){
                            alarm_manager.cancel(pendingIntent);
                            pendingIntent.cancel();
                        }

                    } catch (SQLiteException se){
                        Toast.makeText(context.getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //((Switch) view).setChecked(false);


                    Log.d("고장난거 잡기 해제",String.valueOf(((Switch) view).isChecked()));
                    good.setAlarm(0);
                    notifyDataSetChanged();

                }

                Log.d("요놈","됨 ㅋㅋ");
                notifyDataSetChanged();

            }
        });



    }


}