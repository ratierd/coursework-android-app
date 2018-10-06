package david.ratier.coursework.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import david.ratier.coursework.R;
import david.ratier.coursework.view.MainActivity;

/**
 * Created by David on 28/02/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadingSpinner = (ProgressBar) findViewById(R.id.splash_loading_spinner);
        loadingSpinner.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = MainActivity.getLaunchingIntent(getApplicationContext(), "");
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

}
