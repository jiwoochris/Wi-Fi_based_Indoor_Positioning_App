package sensorteam10.gachon.wi_fi_based_indoor_positioning_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        addDatabase("f9:d1:31:79:99:99", 86, "f8:d3:31:79:33:33", 122, "f1:d5:31:79:33:33", 134,
                54, "401호", db);

    }

    void addDatabase(String ap1, int ap1Strength, String ap2, int ap2Strength, String ap3, int ap3Strength,
                     int altitude, String location, FirebaseFirestore db){
        Map<String, Object> user = new HashMap<>();

        user.put("AP1_MAC", ap1);
        user.put("AP1_RSSI", ap1Strength);

        user.put("AP2_MAC", ap2);
        user.put("AP2_RSSI", ap2Strength);

        user.put("AP3_MAC", ap3);
        user.put("AP3_RSSI", ap3Strength);

        user.put("altitude", altitude);

        user.put("location", location);


        db.collection("finger_printing")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d("dd", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("dd", "Error adding document", e);
                });

    }
}