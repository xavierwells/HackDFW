package com.latheabusaid.hackdfw2019;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Button spot1;
        spot1 = (Button) findViewById(R.id.button);

        Button spot2;
        spot2 = (Button) findViewById(R.id.button2);
        //if(bool=true) spot2.setBackgroundColor(red);


    }
}
