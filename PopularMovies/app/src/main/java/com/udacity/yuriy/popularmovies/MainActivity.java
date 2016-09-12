package com.udacity.yuriy.popularmovies;

import android.app.Activity;
import android.os.Bundle;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.File;

import layout.OverviewFragment;

public class MainActivity extends Activity {

    private static final String DB_PATH = "data/data/com.udacity.yuriy.popularmovies/databases/PosterMovies.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu);

        if (savedInstanceState == null) doDBCheck();

        getFragmentManager().beginTransaction()
                .add(R.id.container, new OverviewFragment())
                .commit();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putByte("DATABASE_DONT_DELETE", (byte)1);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void doDBCheck()
    {
        try{
            File file = new File(DB_PATH);
            file.delete();
        }catch(Exception ex)
        {}
    }
}