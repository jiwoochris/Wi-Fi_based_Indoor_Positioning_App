package com.example.wifimanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button learnButton;
    private Button locateButton;
    private Button syncButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view) {

        Button button= (Button) view;
        Intent intent;
        switch (button.getId()) {
            case R.id.learn_button:
                switchToPositionsActivity("AI");
                break;

            case R.id.locate_button:
                intent = new Intent(MainActivity.this, Locate.class);
                startActivity(intent);
                break;

            default:
                break;

        }

    }

    private void switchToPositionsActivity(String data) {
        Intent intent = new Intent(this, PositionActivity.class);
        intent.putExtra("BUILDING_NAME", data);
        startActivity(intent);
    }

}