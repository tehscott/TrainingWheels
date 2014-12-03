package group5.cs3750.trainingwheels;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import java.lang.Override;

import java.lang.Thread;


public class Splash extends Activity {
    MediaPlayer ring;
    @Override
    protected void onCreate(Bundle splashScreen) {
        super.onCreate(splashScreen);
        setContentView(R.layout.splash);
        final ImageView imageI=(ImageView)findViewById(R.id.letterI);
        final ImageView imageD=(ImageView)findViewById(R.id.letterD);
        final ImageView imageE=(ImageView)findViewById(R.id.letterE);
        final ImageView imageA=(ImageView)findViewById(R.id.letterA);
        final ImageView[] viewArray = {imageI,imageD, imageE,imageA};
        ring = MediaPlayer.create(Splash.this, R.raw.splashsound);

        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final boolean sound = getPrefs.getBoolean("sound", true);

        Thread timer = new Thread(){
            public void run(){
                try{
                    final float ROTATE_FROM = 0.0f;
                    final float ROTATE_TO = 365.0f;

                    Animation anim = new RotateAnimation(ROTATE_FROM, ROTATE_TO,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(0);
                    anim.setDuration(1000);

                   // ring.start();
                    imageI.startAnimation(anim);
                    imageD.startAnimation(anim);
                    imageE.startAnimation(anim);
                    imageA.startAnimation(anim);


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(sound){
                            sleep(500);
                            ring.start();
                        }
                        sleep(4000);
                        Intent ourIntent = new Intent(Splash.this, MainMenu.class);
                        startActivity(ourIntent);
                    }catch(Exception e){

                    }
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