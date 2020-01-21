package com.example.smartplayeractivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keepers="";

    private MediaPlayer myMediaPlayer;
    private ArrayList<File> mySongs;
    private int position;
    private String mSongName ,mode="ON";

    private ImageView playPauseBtn,nextBtn,previousBtn;
    private ImageView imageView;
    private RelativeLayout lower;
    private TextView songNameTxt;
    private Button voiceEnableBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();

        parentRelativeLayout=findViewById(R.id.parentRelativeLayout);
        previousBtn=findViewById(R.id.previous_btn);
        playPauseBtn=findViewById(R.id.play_pause_btn);
        nextBtn=findViewById(R.id.next_btn);
        voiceEnableBtn=findViewById(R.id.voice_enable_btn);
        lower=findViewById(R.id.lower);
        imageView=findViewById(R.id.logo);
        songNameTxt=findViewById(R.id.songName);
        speechRecognizer=speechRecognizer.createSpeechRecognizer(MainActivity.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveValueAndStartPlaying();
        imageView.setBackgroundResource(R.drawable.logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results)
            {
                   ArrayList<String> matchesFound=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                   keepers=matchesFound.get(0);

                if (mode.equals("ON"))
                {
                    if (keepers.equals("pause the song")||keepers.equals("pause"))
                    {
                        playPauseSong();
                        Toast.makeText(MainActivity.this , "Command : "+keepers , Toast.LENGTH_LONG).show();
                    }
                    else if (keepers.equals("play the song")||keepers.equals("play"))
                    {
                        playPauseSong();
                        Toast.makeText(MainActivity.this , "Command : "+keepers , Toast.LENGTH_LONG).show();
                    }
                    else if (keepers.equals("play next song")||keepers.equals("next song"))
                    {
                        playNextSong();
                        Toast.makeText(MainActivity.this , "Command : "+keepers , Toast.LENGTH_LONG).show();
                    }
                    else if (keepers.equals("play previous song")||keepers.equals("previous song"))
                    {
                        playPreviousSong();
                        Toast.makeText(MainActivity.this , "Command : "+keepers , Toast.LENGTH_LONG).show();
                    }

                }



            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType , Bundle params) {

            }
        });

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v , MotionEvent event) {

                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keepers="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }
                return false;



            }
        });



        voiceEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mode.equals("ON"))
                {
                    mode="OFF";
                    voiceEnableBtn.setText("Voice Enable Mode- OFF");
                    lower.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode="ON";
                    voiceEnableBtn.setText("Voice Enable Mode- ON");
                    lower.setVisibility(View.GONE);
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                playPauseSong();
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myMediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(myMediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }

            }
        });
    }
    private  void validateReceiveValueAndStartPlaying()
    {
        if (myMediaPlayer!= null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();

        mySongs=(ArrayList)bundle.getParcelableArrayList("song");
        mSongName=mySongs.get(position).getName();
        String songName=intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position=bundle.getInt("position",0);

        Uri uri=Uri.parse(mySongs.get(position).toString());

        myMediaPlayer=MediaPlayer.create(MainActivity.this, uri);
        myMediaPlayer.start();



    }
    public void checkVoiceCommandPermission()
    {
          if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
          {
              if(!(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED))
              {
                  Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package"+getPackageName()));
                  startActivity(intent);
                  finish();
              }
          }
    }
    private void playPauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);
        if (myMediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();


        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }
    }
    private void playNextSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.start();
        myMediaPlayer.release();

        position=((position+1)% mySongs.size());
        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(MainActivity.this, uri);
        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        if (myMediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.play);

        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            imageView.setBackgroundResource(R.drawable.five);
        }
        myMediaPlayer.start();
        imageView.setBackgroundResource(R.drawable.three);
    }
    private void playPreviousSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.start();
        myMediaPlayer.release();

        position=((position-1)<0?(mySongs.size()-1):(position-1));
        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(MainActivity.this, uri);
        mSongName=mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        if (myMediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.play);

        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.pause);
            imageView.setBackgroundResource(R.drawable.five);
        }

        myMediaPlayer.start();
        imageView.setBackgroundResource(R.drawable.two);



    }


}

