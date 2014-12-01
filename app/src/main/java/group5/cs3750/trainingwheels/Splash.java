package group5.cs3750.trainingwheels;

import android.app.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
        ring = MediaPlayer.create(Splash.this, R.raw.ring);



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
                    imageI.startAnimation(anim);
                    imageD.startAnimation(anim);
                    imageE.startAnimation(anim);
                    imageA.startAnimation(anim);


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        sleep(1500);
                        Intent ourIntent = new Intent("group5.cs3750.trainingwheels.DEMOMENU");
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