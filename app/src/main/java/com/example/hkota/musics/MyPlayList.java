package com.example.hkota.musics;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyPlayList extends AppCompatActivity {

    public static Handler handler;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView recyclerView;

    List arrayList = new ArrayList();

    Button play;

    int time = 0;

    public static Button playbtn, pausebtn, unpausebtn, stopbtn;

    List copyArrayList = new ArrayList();

    Context context = this;

    public static TextView currentsong;

    public static FirebaseRecyclerAdapter firebaseRecyclerAdapterUpdate;

    public static String pause_status = "false";
    public static String unpause_status = "false";
    public static String stop_status = "false";

    public static List<Button> buttonArrayList = new ArrayList<>();

    public static Iterator iterator;

    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_play_list);


        pausebtn = findViewById(R.id.pausebtn);
        unpausebtn = findViewById(R.id.unpausebtn);
        stopbtn = findViewById(R.id.stopbtn);

        MyPlayList myPlayList = new MyPlayList();
        myPlayList.blockButtons("onCreatePosition");


        currentsong = findViewById(R.id.currentsong_textview);

        recyclerView = findViewById(R.id.myrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User").child(CurrentUser.currentUser.getEmail()).child("PlayList");


        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyClass.setPause("true");

            }
        });

        unpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyClass.setUnpause("true");

            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MyClass.setStatus("true");

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        final Context context = this;

        FirebaseRecyclerOptions<Model> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(databaseReference, Model.class)
                .build();


        FirebaseRecyclerAdapter<Model, AdapterForMyPlayList> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, AdapterForMyPlayList>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final AdapterForMyPlayList holder, final int position, @NonNull final Model model) {

                holder.name.setText(model.getName());
                holder.song.setText(model.getSong());


                MyPlayList.buttonArrayList.add(holder.play);


                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MyClass.setUrl(model.getUrl());
                        MyClass.setContext(context);

                        MyClass myClass = new MyClass();
                        Thread thread = new Thread(myClass);

                        MyPlayList myPlayList = new MyPlayList();

                        MyClass.setMyPlayList(myPlayList);

                        thread.start();

                        currentsong.setText(model.getName() + " - " + model.getSong());


                        myPlayList.blockButtons("start");


                    }
                });

            }

            @NonNull
            @Override
            public AdapterForMyPlayList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myplaylist_song, viewGroup, false);
                AdapterForMyPlayList adapter = new AdapterForMyPlayList(view);
                return adapter;
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();
    }

    static class MyClass implements Runnable {

        public static String url = "";
        public static Context context = null;
        //        MediaPlayer mediaPlayer;
        public static String status = "false";
        public static String pause = "false";
        public static String unpause = "false";

        public static String working = "false";

        public static MyPlayList myPlayList;


        @Override
        public void run() {

            MyClass.setWorking("true");


            mediaPlayer = new MediaPlayer();
            mediaPlayer = MediaPlayer.create(context, Uri.parse(MyClass.getUrl()));
            mediaPlayer.start();

            while (mediaPlayer.isPlaying()) {
                while (MyClass.getStatus().equals("true")) {
                    MyClass.getMyPlayList().blockButtons("stop");
                    mediaPlayer.stop();
                    MyClass.setUnpause("false");
                    MyClass.setPause("false");

                    MyClass.setStatus("false");

                    MyPlayList.setCurrentsong("");
                    break;
                }
                while (MyClass.getPause().equals("true")) {
                    mediaPlayer.pause();
                    MyClass.getMyPlayList().blockButtons("pause");


                    while (MyClass.getUnpause().equals("true")) {
                        MyClass.getMyPlayList().blockButtons("unpause");
                        mediaPlayer.start();
                        MyClass.setUnpause("false");
                        MyClass.setPause("false");
                        break;
                    }

                    while (MyClass.getPause().equals("true") && MyClass.getStatus().equals("true")) {
                        MyClass.getMyPlayList().blockButtons("stop");
                        mediaPlayer.stop();
                        MyClass.setUnpause("false");
                        MyClass.setPause("false");
                        MyClass.setStatus("false");
                        MyPlayList.setCurrentsong("");
                        break;
                    }
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        MyClass.getMyPlayList().blockButtons("stop");
                        MyPlayList.setCurrentsong("");
                    }
                });
            }

            MyClass.setWorking("false");


        }

        public static MyPlayList getMyPlayList() {
            return myPlayList;
        }

        public static void setMyPlayList(MyPlayList myPlayList) {
            MyClass.myPlayList = myPlayList;
        }

        public static String getUrl() {
            return url;
        }

        public static void setUrl(String url) {
            MyClass.url = url;
        }

        public static Context getContext() {
            return context;
        }

        public static void setContext(Context context) {
            MyClass.context = context;
        }

        public static String getStatus() {
            return status;
        }

        public static void setStatus(String status) {
            MyClass.status = status;
        }

        public static String getPause() {
            return pause;
        }

        public static void setPause(String pause) {
            MyClass.pause = pause;
        }

        public static String getUnpause() {
            return unpause;
        }

        public static void setUnpause(String unpause) {
            MyClass.unpause = unpause;
        }

        public static String getWorking() {
            return working;
        }

        public static void setWorking(String working) {
            MyClass.working = working;
        }


    }

    public static TextView getCurrentsong() {
        return currentsong;
    }

    public static void setCurrentsong(String currentsong) {
        MyPlayList.currentsong.setText(currentsong);
    }


    public static Iterator getIterator() {
        return iterator;
    }

    public static void setIterator(Iterator iterator) {
        MyPlayList.iterator = iterator;
    }

    public void blockButtons(String text) {

        switch (text) {

            case "onCreatePosition": {


                new Thread() {
                    public void run() {
                        MyPlayList.this.runOnUiThread(new Runnable() {
                            public void run() {


                                pausebtn.setEnabled(false);
                                unpausebtn.setEnabled(false);
                                stopbtn.setEnabled(false);


                            }
                        });
                    }
                }.start();


                break;
            }
            case "start": {


                {
                    new Thread() {
                        public void run() {
                            MyPlayList.this.runOnUiThread(new Runnable() {
                                public void run() {


                                    unpausebtn.setEnabled(false);
                                    stopbtn.setEnabled(true);
                                    pausebtn.setEnabled(true);


                                    Iterator iterator = MyPlayList.buttonArrayList.iterator();

                                    while (iterator.hasNext()) {
                                        Button button = (Button) iterator.next();
                                        button.setEnabled(false);
                                    }


                                }
                            });
                        }
                    }.start();
                }


                break;
            }
            case "pause": {

                {
                    new Thread() {
                        public void run() {
                            MyPlayList.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    pausebtn.setEnabled(false);
                                    unpausebtn.setEnabled(true);


                                }
                            });
                        }
                    }.start();
                }


                break;
            }
            case "unpause": {

                {
                    new Thread() {
                        public void run() {
                            MyPlayList.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    unpausebtn.setEnabled(false);
                                    pausebtn.setEnabled(true);
                                }
                            });
                        }
                    }.start();
                }


                break;
            }
            case "stop": {

                {
                    new Thread() {
                        public void run() {
                            MyPlayList.this.runOnUiThread(new Runnable() {
                                public void run() {


                                    pausebtn.setEnabled(false);
                                    unpausebtn.setEnabled(false);
                                    stopbtn.setEnabled(false);


//                                    List<Button> list = new ArrayList<>();
//                                    list = MyPlayList.buttonArrayList;
                                    Iterator iterator = MyPlayList.buttonArrayList.iterator();

                                    while (iterator.hasNext()) {
                                        Button button = (Button) iterator.next();
                                        button.setEnabled(true);
                                    }

                                    MyPlayList.mediaPlayer.release();
                                    MyPlayList.mediaPlayer = null;


                                }
                            });
                        }
                    }.start();
                }


                break;
            }
            default: {
//                Toast.makeText(MyPlayList.this,"default",Toast.LENGTH_SHORT).show();
                break;
            }
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (mediaPlayer != null) {

            if (MyClass.getPause().equals("true")) {
                MyClass.setStatus("true");
            }


            if (mediaPlayer.isPlaying() && MyClass.getPause().equals("false")) {
                MyClass.setStatus("true");
            }


        }
    }
}