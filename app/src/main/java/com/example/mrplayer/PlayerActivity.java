package com.example.mrplayer;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity
{
    Button play,pause,prev,next;
    TextView songText;
    SeekBar sb;
    String sname;

    static MediaPlayer myMediaPlayer;
    int position;

    ArrayList<File> mySong;
    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        pause=(Button)findViewById(R.id.pause);
        next=(Button)findViewById(R.id.next);
        prev=(Button)findViewById(R.id.prev);
        songText=(TextView)findViewById(R.id.song_name);
        sb=(SeekBar)findViewById(R.id.seekbar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekBar=new Thread()
        {
            @Override
            public void run()
            {
                int totalDuration=myMediaPlayer.getDuration();
                int currentPosition=0;
                while(currentPosition<=totalDuration)
                {
                    try
                    {
                        sleep(500);
                        currentPosition=myMediaPlayer.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        };


        if(myMediaPlayer!=null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();

        mySong=(ArrayList) bundle.getParcelableArrayList("song");

        sname=mySong.get(position).getName().toString();

        String songName=i.getStringExtra("songname");

        songText.setText(songName);
        songText.setSelected(true);
        position=bundle.getInt("pos",0);

        Uri u=Uri.parse(mySong.get(position).toString());
        myMediaPlayer= MediaPlayer.create(getApplicationContext(),u);
        myMediaPlayer.start();

        sb.setMax(myMediaPlayer.getDuration());

        updateSeekBar.start();
        sb.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        sb.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                myMediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sb.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying())
                {
                    pause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                }
                else
                {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                myMediaPlayer.stop();
                myMediaPlayer.release();
                position=((position+1)%mySong.size());
                Uri u=Uri.parse(mySong.get(position).toString());

                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySong.get(position).getName().toString();
                songText.setText(sname);

                myMediaPlayer.start();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                myMediaPlayer.stop();
                myMediaPlayer.release();

                position =((position-1)<0)?(mySong.size()-1):(position-1);
                Uri u=Uri.parse(mySong.get(position).toString());

                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySong.get(position).getName().toString();
                songText.setText(sname);

                myMediaPlayer.start();

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
