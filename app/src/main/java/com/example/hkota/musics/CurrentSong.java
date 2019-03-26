package com.example.hkota.musics;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class CurrentSong extends AppCompatActivity{

    public Context context = this;

    Button playBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    public static TextView before_time;
    public static TextView after_time;
    public static MediaPlayer mp;
    int totalTime;

    public static int time;

//    MediaPlayer mediaPlayer;

    int number = 0;

    Toolbar toolbar;

    Button add;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

//    public static String url = "";

//    Button playBtn;

    public static String flag = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_song);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User").child(CurrentUser.currentUser.getEmail()).child("PlayList");



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        playBtn = (Button) findViewById(R.id.playBtn);
        before_time = (TextView) findViewById(R.id.before_time);
        after_time = (TextView) findViewById(R.id.after_time);
        positionBar = findViewById(R.id.positionBar);


    }


    public void playBtnClick(View view) {

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");


        if (CurrentSong.mp==null){

            Toast.makeText(CurrentSong.this,"Загрузка песни...",Toast.LENGTH_SHORT).show();

            CurrentSong.mp = new MediaPlayer();
            CurrentSong.mp = MediaPlayer.create(CurrentSong.this,Uri.parse(url));
            CurrentSong.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    playBtn.setBackgroundResource(R.drawable.play);

                }
            });

            time = CurrentSong.mp.getDuration();

            after_time.setText(String.valueOf(time));

            positionBar.setMax(time);
            positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mp.seekTo(progress);
                        positionBar.setProgress(progress);


                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });



            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (CurrentSong.mp != null) {



                        try{
                            Message msg = new Message();
                            msg.what = mp.getCurrentPosition();
                            handler.sendMessage(msg);

                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }).start();






            Toast.makeText(CurrentSong.this,"Загрузка песни...",Toast.LENGTH_SHORT).show();

        }


        if (!mp.isPlaying()) {
            // Stopping
            mp.start();
            playBtn.setBackgroundResource(R.drawable.stop);

        } else {
            // Playing
            mp.pause();
            playBtn.setBackgroundResource(R.drawable.play);
        }


    }


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.one:{

                Intent intent = getIntent();

                final String url = intent.getStringExtra("url");
                final String song = intent.getStringExtra("playlist_song");
                final String name = intent.getStringExtra("name");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child(name+song).exists()){
                            databaseReference.child(name+song).child("name").setValue(name);
                            databaseReference.child(name+song).child("song").setValue(song);
                            databaseReference.child(name+song).child("url").setValue(url);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();


        CurrentSong.flag = "true";


        if (CurrentSong.mp != null) {
            try {


                CurrentSong.mp.release();
                CurrentSong.mp = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;

            positionBar.setProgress(currentPosition);


            String elapsedTime = createTimeLabel(currentPosition);
            before_time.setText(String.valueOf(elapsedTime));

            String remainingTime = createTimeLabel(time - currentPosition);
            after_time.setText("- " + String.valueOf(remainingTime));
        }
    };

    public String createTimeLabel(int time) {

        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;



        return timeLabel;
    }

    public static String getFlag() {
        return flag;
    }

    public static void setFlag(String flag) {
        CurrentSong.flag = flag;
    }
}