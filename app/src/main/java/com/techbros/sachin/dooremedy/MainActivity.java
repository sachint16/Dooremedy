package com.techbros.sachin.dooremedy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sm=new SessionManager(this);
        boolean status=sm.isLoggedIn();

ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            }
        }, 0, 3000);


        if(status){
            Intent i=new Intent(MainActivity.this,HomeActivity.class);
            startActivity(i);
        }
        else{
            Intent i=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(i);
        }
    }
}
