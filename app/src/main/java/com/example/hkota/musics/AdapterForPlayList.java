package com.example.hkota.musics;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AdapterForPlayList extends RecyclerView.ViewHolder {

    View view;
    TextView name;
    TextView song;
    ImageButton play;

    public AdapterForPlayList(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        name = itemView.findViewById(R.id.name_textview);
        song = itemView.findViewById(R.id.song_textview);
        play = itemView.findViewById(R.id.play_button);
    }
}
