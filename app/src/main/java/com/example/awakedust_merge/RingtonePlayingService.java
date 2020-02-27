package com.example.awakedust_merge;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;
import static com.example.awakedust_merge.TimerSetting.REQUEST_CODE_RINGTONE;

public class RingtonePlayingService extends Service{

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;
    boolean ispush=true;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Log.d("isOk","되나고야");
            Toast.makeText(RingtonePlayingService.this,"지정하신 알람시간 입니다.", Toast.LENGTH_SHORT).show();

            Notification notification = new NotificationCompat
                    .Builder(this,CHANNEL_ID)
                    .setContentTitle("알람시작")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String getState = intent.getExtras().getString("state");
        String uri = intent.getExtras().getString("alarm_uri");





        assert getState != null;
        switch (getState) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if(!this.isRunning && startId == 1) {
            Log.d("where?","a");

            try{
                mediaPlayer.setDataSource(this,Uri.parse(uri));
                Log.d("알람 여기까진",String.valueOf(uri));
                startAlarm(mediaPlayer);
            }catch (Exception e){
                try{
                Uri second = Uri.parse("android.resource://com.example.awakedust_merge/raw/testzz");
                mediaPlayer.setDataSource(this,second);
                startAlarm(mediaPlayer);
                }catch (Exception e1){Log.d("에러",String.valueOf(String.valueOf(e1)));}
            }
            //this.isRunning = true;
            this.startId = 0;
            return START_STICKY;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if(this.isRunning && startId == 0) {
            Log.d("where?","b");
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            this.isRunning = false;
            this.startId = 0;
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if(!this.isRunning && startId == 0) {
            Log.d("where?","c");

            if(this.ispush) {
                stopMusic();
                this.isRunning = false;
                this.startId = 0;
                ispush=false;
            }

        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if(this.isRunning && startId == 1){
            Log.d("where?","d");
            this.isRunning = true;
            this.startId = 1;
        }

        else {
            Log.d("where?","e");
        }
        return START_STICKY;
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();     // 5. 재생 중지
            mediaPlayer.release();    // 6. MediaPlayer 리소스 해제
      //      mediaPlayer = null;
        }

    }

    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {   // 현재 Alarm 볼륨 구함
            player.setAudioStreamType(AudioManager.STREAM_ALARM);    // Alarm 볼륨 설정
            player.setLooping(true);    // 음악 반복 재생
            player.prepare();   // 3. 재생 준비
            player.start();    // 4. 재생 시작
    //    }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onDestory() 실행", "서비스 파괴");

    }

   /* private MediaPlayer mMediaPlayer;   // MediaPlayer 변수 선언




    private void playMusic() {
        stopMusic();  // 플레이 할 때 가장 먼저 음악 중지 실행
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);  // 기본 벨소리(알람)의 URI
        mMediaPlayer = new MediaPlayer();  ​​  // 1. MediaPlayer 객체 생성

        try {
            mMediaPlayer.setDataSource(this, alert);  // 2. 데이터 소스 설정 (인터넷에 있는 음악 파일도 가능함)
            startAlarm(mMediaPlayer);
        } catch (Exception ex) {
            try {
                mMediaPlayer.reset();    // MediaPlayer의 Error 상태 초기화
                startAlarm(mMediaPlayer);
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }



    private void startAlarm(MediaPlayer player) throws java.io.IOException, IllegalArgumentException, IllegalStateException {

        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {   // 현재 Alarm 볼륨 구함
            player.setAudioStreamType(AudioManager.STREAM_ALARM);    // Alarm 볼륨 설정
            player.setLooping(true);    // 음악 반복 재생
            player.prepare();   // 3. 재생 준비
            player.start();    // 4. 재생 시작
        }
    }



    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();     // 5. 재생 중지
            mMediaPlayer.release();    // 6. MediaPlayer 리소스 해제
            mMediaPlayer = null;
        }
    }



    public void onDestroy() {
        super.onDestroy();
        stopMusic();    // 음악 중지.
    }*/
}
