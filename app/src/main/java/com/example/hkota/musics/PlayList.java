package com.example.hkota.musics;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PlayList extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ArrayList arrayList = new ArrayList();

    RecyclerView recyclerView;

    Button myplaylist;

    public static MediaPlayer mp;

    CurrentSong currentSong;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        floatingActionButton = findViewById(R.id.myplaylist_button);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Music");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PlayList.this, MyPlayList.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(databaseReference, Model.class)
                .build();


        FirebaseRecyclerAdapter<Model, AdapterForPlayList> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, AdapterForPlayList>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final AdapterForPlayList holder, final int position, @NonNull final Model model) {

                holder.name.setText(model.getName() + " - ");
                holder.song.setText(model.getSong());

                arrayList.add(model.getUrl());

                holder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String songUrl = (String) arrayList.get(position);



                        Intent intent = new Intent(PlayList.this, CurrentSong.class);
                        intent.putExtra("url", songUrl);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("playlist_song", model.getSong());
                        startActivity(intent);



                    }
                });

            }

            @NonNull
            @Override
            public AdapterForPlayList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_song, viewGroup, false);
                AdapterForPlayList adapter = new AdapterForPlayList(view);
                return adapter;
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}
