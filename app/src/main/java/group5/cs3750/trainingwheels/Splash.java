package group5.cs3750.trainingwheels;

import android.app.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.lang.InterruptedException;
import java.lang.Override;

import java.lang.Thread;


public class Splash extends Activity {
    MediaPlayer ring;
    @Override
    protected void onCreate(Bundle splashScreen) {
        super.onCreate(splashScreen);
        setContentView(R.layout.splash);

        ring = MediaPlayer.create(Splash.this, R.raw.ring);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1800);
                    ring.start();
                    sleep(2500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent ourIntent = new Intent("group5.cs3750.trainingwheels.TUTORIAL");
                    startActivity(ourIntent);
                }
            }
        };
        timer.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        ring.release();
        finish();
    }
}