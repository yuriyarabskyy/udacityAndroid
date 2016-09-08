package com.example.yuriy.portfolio;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String[] toasts = {"Label", "First Application Soon", "Second App Later", "Third App Possible",
    "Make your app material", "Go ubiquitous", "Capstone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = (LinearLayout) findViewById(R.id.menu);

        int n = layout.getChildCount();

        for (int i = 0; i < n; i++) {
            if (!(layout.getChildAt(i) instanceof Button)) continue;
            Button button = (Button) layout.getChildAt(i);
            final String str = toasts[i];
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, str, duration);
                    toast.show();
                }
            });
        }
    }
}
