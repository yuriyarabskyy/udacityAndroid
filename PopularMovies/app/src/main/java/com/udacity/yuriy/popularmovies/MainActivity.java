package com.udacity.yuriy.popularmovies;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.OverviewFragment;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new OverviewFragment())
                    .commit();
        }
    }
}
