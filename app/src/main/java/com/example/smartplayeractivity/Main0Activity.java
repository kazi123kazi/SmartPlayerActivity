package com.example.smartplayeractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main0Activity extends AppCompatActivity {
    private  String[] itemsAll;
    private ListView mSongList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);
        mSongList=findViewById(R.id.songsList);

        externalStoragePermission();


    }
    public void externalStoragePermission()
    {
        Dexter.withActivity(Main0Activity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                          displayAudioSongName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File> arrayList=new ArrayList<>();

        File[] allFiles=file.listFiles();

        for ( File individualFile : allFiles)
        {
            if (individualFile.isDirectory() && !individualFile.isHidden())
            {
                arrayList.addAll(readOnlyAudioSongs(individualFile));

            }
            else
            {
               if(individualFile.getName().endsWith(".mp3")||individualFile.getName().endsWith(".aac")||
                       individualFile.getName().endsWith(".wma")||individualFile.getName().endsWith(".wav"))
               {
                   arrayList.add(individualFile);

               }
            }
        }



        return arrayList;
    }

    private void displayAudioSongName()
    {
         final ArrayList<File> audioSongs=readOnlyAudioSongs(Environment.getExternalStorageDirectory());

         itemsAll=new String[audioSongs.size()];
         for (int songCounter=0;songCounter<audioSongs.size();songCounter++)
         {
             itemsAll[songCounter]=audioSongs.get(songCounter).getName();

         }
         ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(Main0Activity.this , android.R.layout.simple_list_item_1 , itemsAll);
         mSongList.setAdapter(arrayAdapter);
         mSongList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent , View view , int position , long id)
             {
                 String songName=mSongList.getItemAtPosition(position).toString();
                 Intent intent=new Intent(Main0Activity.this,MainActivity.class);
                 intent.putExtra("song",audioSongs);
                 intent.putExtra("position",position);
                 intent.putExtra("name",songName);
                 startActivity(intent);
             }
         });

    }
}
