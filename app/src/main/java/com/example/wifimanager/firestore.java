package com.example.wifimanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class firestore extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION =  1;
    ArrayList<Realtime> DataList;
    ArrayList<FireStoreDB> CollectionsInorder ;
    ArrayList<Realtime> PositioningInorder ;
    ArrayList<FireStoreDB> fireStoreInorder;
    Button localization;
    TextView textView ;
    WifiManager wifi;

    private void scanSuccess() {    // Wifi검색 성공
        List<ScanResult> results = wifi.getScanResults();
        PositioningInorder = new ArrayList<>();
        Realtime temp= new Realtime();
        Log.d("firestoreActivity value", results.toString());
        for (int i = 0; i < results.size(); i++) {
            // System.out.println("test2");
            String ssid0 = results.get(i).SSID;
            String bssid = results.get(i).BSSID;
            int rssi0 = results.get(i).level;
            temp.setSsid(ssid0);
            temp.setMac_id(bssid);
            temp.setRssi(rssi0);
            PositioningInorder.add(temp);

        }
        Collections.sort(PositioningInorder, new RealtimeComparator()); //소팅까지 완료
        ArrayList<Integer> cnt = new ArrayList<>();
        for(int i=0; i<3170; i+=5){
            int num=0;
            for (int j=0; j<5; j++){
                FireStoreDB c =CollectionsInorder.get(i+j);
                for (int k=0; k<5; j++){
                    Realtime p= PositioningInorder.get(k);
                    if(c.getMac_id().equals(p.getMac_id())){
                        num++;
                    }
                }


            }cnt.add(num);
        }
        String pos="out of range";
        Log.d("here", "위치 찾으러가는중");
        for(int i=0; i<cnt.size(); i++){
            if(cnt.get(i)==1){
                FireStoreDB r= CollectionsInorder.get(i*5-1);
                pos=(r.getPosition_id());
                break;
            }

        }
        for(int i=0; i<cnt.size(); i++){
            if(cnt.get(i)==2){
                FireStoreDB r= CollectionsInorder.get(i*5-1);
                pos=(r.getPosition_id());
                break;
            }

        }
        for(int i=0; i<cnt.size(); i++){
            if(cnt.get(i)==3){
                FireStoreDB r= CollectionsInorder.get(i*5-1);
                pos=(r.getPosition_id());
                break;
            }

        }
        for(int i=0; i<cnt.size(); i++){
            if(cnt.get(i)==4){
                FireStoreDB r= CollectionsInorder.get(i*5-1);
                pos=(r.getPosition_id());
                break;
            }

        }
        for(int i=0; i<cnt.size(); i++){
            if(cnt.get(i)==5){
                FireStoreDB r= CollectionsInorder.get(i*5-1);
                pos=(r.getPosition_id());

                break;
            }

        }
        textView.setText(pos);


    }

    private void scanFailure() {    // Wifi검색 실패
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localization);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        localization = (Button) findViewById(R.id.localization_btn);
            textView= (TextView)findViewById(R.id.localization_tv) ;
//        CSVReader csv = new CSVReader();
//        csv.csvList;
        if (ContextCompat.checkSelfPermission(firestore.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission","checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(firestore.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                Log.d("permission","shouldShowRequestPermissionRationale");
                // 사용자에게 설명을 보여줍니다.
                // 권한 요청을 다시 시도합니다.

            } else {
                // 권한요청

                Log.d("permission","권한 요청");
                ActivityCompat.requestPermissions(firestore.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE},
                        1000);

            }
        }
        setRealtimeArray();
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                    Log.e("wifi","scanSuccess !!!!!!!!!!!!!!!");
                } else {
                    // scan failure handling
                    scanFailure();
                    Log.e("wifi","scanFailure ..............");
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);


        localization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = wifi.startScan();
                if (!success) {
                    // scan failure handling
                    scanFailure();
                    Log.e("wifi","scanFailure ..............");
                }
            }
        });





    }

//    private void getCollection(Realtime temp) {
//        FirebaseFirestore fb = FirebaseFirestore.getInstance();
//        CollectionReference collectionReference = fb.collection("data");
//        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    int i = 0;
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        //document.getData() or document.getId() 등등 여러 방법으로
//                        //데이터를 가져올 수 있다.
//                        String mac_id = (String) document.getData().get("mac_id");
//                        String position_id = (String) document.getData().get("position_id");
//                        String ssid = (String) document.getData().get("ssid");
//                        Long rssi = (Long) document.getData().get("rssi");
//
//                        temp.setPosition_id(position_id);
//                        temp.setSsid(ssid);
//                        temp.setMac_id(mac_id);
//                        temp.setRssi(rssi.intValue());
//                        Log.d("getDocument", position_id + "  " + mac_id + "  " + ssid + "  " + rssi);
//                        DataList.add(temp);
//                        i++;
//
//                    }
//                }
//            }
//        });
//    }

    public void setRealtimeArray(){
        CollectionsInorder = new ArrayList<>(600);
        for (int i =0 ; i<595; i++){
            CollectionsInorder.add(GetDocumentByindex(i));

        }
        Collections.sort(CollectionsInorder, new FireStoreComparator());
    }
    //postition에서 다섯개 데이터 받아오면 CollectionsInorder랑 비교.
    //순서 맞추기 쉬우라고 5개씩 끊게 만듦
    public FireStoreDB GetDocumentByindex(int i) {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fb.collection("top5_ho").document(i + "");
        FireStoreDB r = new FireStoreDB();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {

                            String mac_id = (String) document.getData().get("mac_id");
                            String position_id = (String) document.getData().get("position_id");
                            String ssid = (String) document.getData().get("ssid");
                            Long rssi = (Long) document.getData().get("rssi");
                            r.setPosition_id(position_id);
                            r.setSsid(ssid);
                            r.setMac_id(mac_id);
                            r.setRssi(rssi.intValue());
                            r.setI(i);

                            Log.d("getDocument", i+"  "+position_id + "  " + mac_id + "  " + ssid + "  " + rssi);

                            //여기에 찾을 값들 다 넣으면 됨
                        }
                    }
                }
            }
        }); return r;
    }

    public ArrayList<Realtime> getDataList() {
        return DataList;
    }

    public void setDataList(ArrayList<Realtime> dataList) {
        DataList = dataList;
    }

//    public ArrayList<Realtime> getCollectionsInorder() {
//        return CollectionsInorder;
//    }
//
//    public void setCollectionsInorder(ArrayList<Realtime> collectionsInorder) {
//        CollectionsInorder = collectionsInorder;
//    }
//권한요청을 사용자에게 허락받았는지 못받았는지 결과를 알수 있는 콜백 메서드
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
        case 1000: {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // 권한 획득 성공
                Log.d("permission","권한 획득 성공");

            } else {

                // 권한 획득 실패
                Log.d("permission","권한 획득 실패");
            }
            return;
        }

    }
}
}

