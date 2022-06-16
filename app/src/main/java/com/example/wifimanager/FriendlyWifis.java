package com.example.wifimanager;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class FriendlyWifis extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION =  1;
    //private static final int REQUEST_CODE_ASK_PERMISSIONS = ;
    private Button addWifi;
    WifiManager wifi;
    List<ScanResult> results;
    ListView wifisList;
    protected CharSequence[] options;
    protected boolean[] selections;
    ArrayAdapter<Router> arrayAdapter;
    ArrayList<Router> wifis;
    String building;
    Button save;

    DatabaseHelper db;
//        private void scanSuccess() {
//            results = wifi.getScanResults();
//        Log.d("value", results.toString());
//  // new scan results ...
//    }
//
//    private void scanFailure() {
//        // handle failure: new scan did NOT succeed
//        // consider using old scan results: these are the OLD results!
//        results = wifi.getScanResults();
 // potentially use older scan results ...
 //   }
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_settings);
        db = new DatabaseHelper(this);
        addWifi = (Button) findViewById(R.id.button_add);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        addWifi.setOnClickListener(new ButtonClickHandler());
        save = (Button) findViewById(R.id.save);

        wifisList = (ListView) findViewById(R.id.friendly_wifis);

        Intent intent = getIntent();

//-----------------------------------------------------------------------------------------------------------
//        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context c, Intent intent) {
//                boolean success = intent.getBooleanExtra(
//                        WifiManager.EXTRA_RESULTS_UPDATED, false);
//                if (success) {
//
//                    //scanSuccess();
//                }
//            }
//        };

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.
                    permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                Log.i("Friendly WiFi", "User location NOT ENABLED, waiting for permission");

            } else {
                wifi.setWifiEnabled(true);
//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
//                getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
//                wifi.startScan();
                getAndShowScanResults();
            }

//        if (!success) {
//            // scan failure handling
//            scanFailure();
//        }
            //------------------------------------------------------------------------------------------

            building = intent.getStringExtra("BUILDING_NAME");
            wifis = db.getFriendlyWifis(building);
            arrayAdapter = new ArrayAdapter<Router>(this,
                    android.R.layout.simple_list_item_1, wifis);
            // Set The Adapter
            wifisList.setAdapter(arrayAdapter);
            save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (db.addFriendlyWifis(building, wifis)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Saved :)", Toast.LENGTH_SHORT);
                        toast.show();

                    }


                }
            });

            wifisList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               int arg2, long arg3) {
                    wifis.remove(arg2);

                    arrayAdapter.notifyDataSetChanged();
                    return false;
                }

            });


        }
    }
    public void updateOptions() {
        options = new CharSequence[results.size()];
        for (int i = 0; i < results.size(); i++)
            options[i] = results.get(i).SSID;
        selections = new boolean[options.length];

    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            }else{
                getAndShowScanResults();
            }
        }
    }



//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            getAndShowScanResults();
//        }
//    }

   public void getAndShowScanResults() {
        results = wifi.getScanResults();
       Log.d("value", results.toString());
        updateOptions();
        onCreateDialog(0).show();
    }


    @Override
    public Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this)
                .setTitle("Choose Friendly Wifis")
                .setMultiChoiceItems(options, selections,
                        new DialogSelectionClickHandler())
                .setPositiveButton("OK", new DialogButtonClickHandler())
                .create();
    }

    public class DialogSelectionClickHandler implements
            DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked,
                            boolean selected) {
            Log.i("ME", options[clicked] + " selected: " + selected);
        }
    }

    public class DialogButtonClickHandler implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    updateFriendlyWifis();
                    break;
            }
        }
    }

    public void updateFriendlyWifis() {
        wifis.clear();
        for (int i = 0; i < options.length; i++) {
            if (selections[i]) {
                Router router = new Router(results.get(i).SSID,
                        results.get(i).BSSID);
                if (!wifis.contains(router))
                    wifis.add(router);

            }
            arrayAdapter = new ArrayAdapter<Router>(this,
                    android.R.layout.simple_list_item_1, wifis);
            // Set The Adapter
            wifisList.setAdapter(arrayAdapter);
            Log.i("ME", options[i] + " selected: " + selections[i]);
        }
    }

}
