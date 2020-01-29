package com.example.list_all_songs_from_storage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 1;
    MediaPlayer mp = new MediaPlayer();
    Boolean musicIsPlaying = false;
    ArrayList<String> stringArrayList;
//    ArrayList<String> locationsArrayList;
//    ArrayList<String> arrayList;
//    String[] locationsArray;
    String locationsString = "";
    String[] musicParts;

    ListView musicListView;

    ArrayAdapter<String> stringArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED)
        {

            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }

        } else {
            doStuff();
        }

    }

    public void doStuff() {
        musicListView = (ListView) findViewById(R.id.musicListView);
        stringArrayList = new ArrayList<>();
        getMusic();
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringArrayList);
        musicListView.setAdapter(stringArrayAdapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open music player to play desired song
                Log.i("debugparent", locationsString);
//                Log.i("debugparent", Integer.toString(position));
//                Log.i("debugparent", String.valueOf(position));
//                Log.i("debugparent", musicParts[(int) id]);
//                Log.i("debugparent", Array.toString(musicParts));
//                for (String foo: musicParts) {
//                    Log.i("debugs", foo);
//                }
                playMusic(position);
            }
        });
    }

    private void playMusic(Integer position) {
        if(musicIsPlaying) {
            mp.pause();
            mp.release();
        }
        try {
            mp = new MediaPlayer();
//            mp.setDataSource("/storage/emulated/0/Download/Lil Morty - Choppa on me.mp3");
            mp.setDataSource(musicParts[position]);
            mp.prepare();
            mp.start();
            musicIsPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            int counter = 0;

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songLocation);

//                locationsArrayList.add(currentLocation);
                stringArrayList.add(currentTitle + "\n" + currentArtist);
                locationsString += currentLocation;
                locationsString += "|";
//                stringArrayList.add(currentLocation);
//                locationsArray[counter] = currentLocation;
//                Log.i("currentLocation", currentLocation.getClass().getName());
//                if(currentLocation instanceof String) {
//                    Log.i("Stringggggggggggggggg","messageeeeeeeeeeeeeeeeeeeeeee");
//                    arrayList.add(currentLocation);
//                }
                counter++;
            } while (songCursor.moveToNext());
            musicParts = locationsString.split("\\|");
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        doStuff();
                    } else {
                        Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
            }
        }
    }
}
