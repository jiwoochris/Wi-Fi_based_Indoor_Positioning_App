package com.example.indoorpositioning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartingScreen extends Activity {
    private Button learnButton;
    private Button locateButton;
    private Button syncButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen);
    }


    public void onClick(View view) {

        Button button= (Button) view;
        Intent intent;
        switch (button.getId()) {
            case R.id.learn_button:
                switchToPositionsActivity("AI");
                break;

            case R.id.locate_button:
                intent = new Intent(StartingScreen.this, Locate.class);
                startActivity(intent);
                break;

            default:
                break;

        }

    }

    private void switchToPositionsActivity(String data) {
        Intent intent = new Intent(this, Positions.class);
        intent.putExtra("BUILDING_NAME", data);
        startActivity(intent);
    }

}
