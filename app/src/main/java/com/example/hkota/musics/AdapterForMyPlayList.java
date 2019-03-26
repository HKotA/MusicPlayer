package com.example.hkota.musics;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdapterForMyPlayList extends RecyclerView.ViewHolder {

    View view;
    TextView name;
    TextView song;
    Button play;

    public AdapterForMyPlayList(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        name = itemView.findViewById(R.id.name1_textview);
        song = itemView.findViewById(R.id.song1_textview);
        play = itemView.findViewById(R.id.play1_button);
    }
}
