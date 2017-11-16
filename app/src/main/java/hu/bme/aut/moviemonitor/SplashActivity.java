package hu.bme.aut.moviemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity
{
    private static final int SPLASH_TIME_OUT = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        startAnimations();

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent();

                intent.setClass(SplashActivity.this, MainActivity.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();

            }
        }, SPLASH_TIME_OUT);
    }

    private void startAnimations()
    {
        final Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.animation);

        animation.reset();
        ImageView image = (ImageView) findViewById(R.id.splashImage);
        image.clearAnimation();
        image.startAnimation(animation);
    }
}
