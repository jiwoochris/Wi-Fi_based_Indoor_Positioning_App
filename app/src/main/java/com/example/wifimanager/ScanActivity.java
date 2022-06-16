package com.example.wifimanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wifimanager.PositionData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION =  1;
    private TextView warning;
    private TextView timeRemaining;
    private Button calibrate;
    private int readingCount = 30;
    private int currentCount;
    String currentPositionName;
    WifiManager wifi;
    Timer timer;
    TimerTask myTimerTask;

    private List<ResultData> resultsData;
    private List<PositionData> positionsData;
    private PositionData positionData;

    @SuppressWarnings("null")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        warning = (TextView) findViewById(R.id.TextView1);
        timeRemaining = (TextView) findViewById(R.id.TextView2);
        calibrate = (Button) findViewById(R.id.start);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {

                    List<ScanResult> results = wifi.getScanResults();
                    Log.d("ScanActivity value", results.toString());

                }
            }
        };

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.
                    permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                Log.i("ScanActivity", "User location NOT ENABLED, waiting for permission");

            } else {
                //Start scanning for wifi
            }
            wifi.setWifiEnabled(true);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
            wifi.startScan();

            //--------------------------------------------------------------------------------------------------------
            Intent intent = getIntent();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm...");
            alertDialog.setMessage("Scanning requires WiFi.");
            alertDialog.setPositiveButton("Turn on WiFi",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Activity transfer to wifi settings
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
            alertDialog.setCancelable(false);

            if (!wifi.isWifiEnabled()) {
                alertDialog.show();
            }

            currentPositionName = null;
            if (intent.getBooleanExtra("isLearning", true))
                currentPositionName = intent.getStringExtra("POSITION_NAME");

            calibrate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calibrate.setEnabled(false);
                    warning.setText("DO NOT MOVE FOR");
                    resultsData = new ArrayList<ResultData>();
                    currentCount = 0;
                    timer = new Timer();
//                    for (int i = 0; i<50; i++){
//                        resultsforfilter[i]=new List<ScanResult>();
//                    }
                    myTimerTask = new TimerTask() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            refresh();
                        }
                    };
                    timer.schedule(myTimerTask, 0, 1000); //지정한 시간(firstTime) 부터 일정 간격(period)으로 지정한 작업(task)을 수행한다.
                }
            });


        }
    }
//    List<ScanResult>[] resultsforfilter=new List[50];
    double[][] rssiValues = new double[31][20];
    private void refresh() { //1초 마다 실행
        // TODO Auto-generated method stub
        if (currentCount >= readingCount) {
            if (myTimerTask != null)
                myTimerTask.cancel();

        }
        currentCount++;
        wifi.startScan();


        List<ScanResult> results = wifi.getScanResults();
//        resultsforfilter[currentCount].add(results);
        for (int i = 0; i < results.size(); i++) {
            // System.out.println("test2");
            String ssid0 = results.get(i).SSID;
            String bssid = results.get(i).BSSID;

            int rssi0 = results.get(i).level;

            boolean found = false;
            for (int pos = 0; pos < resultsData.size(); pos++) {
                if (resultsData.get(pos).getRouter().getBSSID().equals(bssid)) {
                    found = true;
                    resultsData.get(pos).values.add(rssi0);
                    break;
                }
            }
            if (!found) {

                ResultData data = new ResultData(new Router(ssid0, bssid));
                data.values.add(rssi0);
                resultsData.add(data);
            }
            // String rssiString0 = String.valueOf(rssi0);
            // textStatus = textStatus.concat("\n" + ssid0 + "   " +
            // rssiString0);
            // System.out.println("ajsdhks"+textStatus);
        }
        // Log.v("textStatus", textStatus);
        // System.out.println(""+textStatus);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // result.setText("here"+currentCount);
                timeRemaining
                        .setText(" " + (readingCount - currentCount) + "s");
                if (currentCount >= readingCount) {
                    returnResults();
                }
            }
        });

    }

    private void returnResults() {
        // TODO Auto-generated method stub
        double filter =0;
        positionData = new PositionData(currentPositionName);
        for (int length = 0; length < resultsData.size(); length++) {

            int sum = 0;
            for (int l = 0; l < resultsData.get(length).values.size(); l++) {
                sum += resultsData.get(length).values.get(l);
                filter = (resultsData.get(length).values.get(l))*0.25;
            }
            int average = sum / resultsData.get(length).values.size();

            positionData.addValue(resultsData.get(length).getRouter(), average);
        }

        Intent intent = new Intent(getApplicationContext(), PositionActivity.class);
        intent.putExtra("PositionData", (Serializable) positionData);
        setResult(RESULT_OK,intent);
        finish();


    }

    public class ResultData {
        private Router router;

        public Router getRouter() {
            return this.router;
        }

        public List<Integer> values;

        public ResultData(Router router) {
            // TODO Auto-generated constructor stub
            this.router = router;
            values = new ArrayList<Integer>();
        }
    }
}