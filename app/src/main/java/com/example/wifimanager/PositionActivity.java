package com.example.wifimanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PositionActivity extends AppCompatActivity {
    private Button calibrate;
    private Button finish;
    private EditText positionName;
    private int readingCount = 30;
    private TextView results;
    private String resultsText;
    private ListView positionsList;
    ArrayList<String> positions;
    ArrayAdapter arrayAdapter;
    Timer timer;
    TimerTask myTimerTask;
    int positionCount;
    DatabaseHelper db;
    private Boolean isLearning = true;
    static final int SCAN_REQUEST = 0;
    Button friendlyWifisButton;

    private List<PositionData> positionsData;
    private PositionData positionData;
    private String building;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);
        friendlyWifisButton= (Button) findViewById(R.id.friendly_wifis_button);
        friendlyWifisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendlyWifis.class);
                intent.putExtra("BUILDING_NAME", building);
                startActivity(intent);
            }
        });

        positionName = (EditText) findViewById(R.id.position_name);
        calibrate = (Button) findViewById(R.id.calibratebutton);
        finish = (Button) findViewById(R.id.finish);
        positionsList = (ListView) findViewById(R.id.positionslist);
        resultsText = "";

        positionCount = 0;
        positionsData = new ArrayList<PositionData>();
        Intent intent = getIntent();
        // readingCount = Integer.parseInt(intent
        // .getStringExtra("NUMBER_OF_SECONDS"));
        building = intent.getStringExtra("BUILDING_NAME");
        db = new DatabaseHelper(this);
        positions = db.getPositions(building);
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, positions);
        positionsList.setAdapter(arrayAdapter);
        positionName.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.equals("")){
                    calibrate.setEnabled(false);
                }
                else
                    calibrate.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        positionName.setText("");
        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(db.getFriendlyWifis(building).isEmpty()) {
                    Context context = getApplicationContext();
                    CharSequence text = "Select one or more Friendly WiFi";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                    intent.putExtra("POSITION_NAME", positionName.getText().toString());
                    intent.putExtra("isLearning", isLearning);
                    intent.putExtra("NUMBER_OF_SECONDS", readingCount);
                    Log.i ("PostionActvity", "calibrate.setonClickListener");
                    startActivityForResult(intent, SCAN_REQUEST);
                }
            }
        });

        positionsList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v,
                                            int position, long id) {
                        Intent intent = new Intent(getApplicationContext(),
                                ScanActivity.class);
                        String selectedPosition = (String) parent
                                .getItemAtPosition(position);
                        intent.putExtra("isLearning", isLearning);
                        intent.putExtra("POSITION_NAME", selectedPosition);
                        intent.putExtra("NUMBER_OF_SECONDS", readingCount);
                        startActivityForResult(intent,SCAN_REQUEST);
                    }
                });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        positionsList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    db.deleteReading(building, positions.get(position));
                                    positions.remove(position);
                                    arrayAdapter.notifyDataSetChanged();

                                }

                            }
                        });
        positionsList.setOnTouchListener(touchListener);

    }

    @Override
    protected void onActivityResult(int request, int result, Intent intent) {
        if(result==RESULT_OK) {
            positionData = (PositionData) intent
                    .getSerializableExtra("PositionData");
            Log.v("Before db : ", positionData.toString());
            db.addReadings(building, positionData);
            positions = db.getPositions(building);
            arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, positions);
            positionsList.setAdapter(arrayAdapter);
            super.onActivityResult(request, result, intent);
        }
    }
    @Override
    protected void onResume() {
        positions = db.getPositions(building);
        arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, positions);
        positionsList.setAdapter(arrayAdapter);
        positionName.setText("");
        calibrate.setEnabled(false);
        super.onResume();
    }



}