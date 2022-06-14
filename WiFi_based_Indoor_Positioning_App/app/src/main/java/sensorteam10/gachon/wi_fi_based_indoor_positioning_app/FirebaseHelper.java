//package sensorteam10.gachon.wi_fi_based_indoor_positioning_app;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//public class FirebaseHelper {
//
//
//    public static final String AP_TABLE = "access_points";
//    public static final String READINGS_TABLE = "readings";
//
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private HashMap hp;
//
//    public FirebaseHelper(Context context) {
//        super();
//    }
//
//    public int deleteReading(String building_id, String position_id) {
//        db.collection(READINGS_TABLE)
//                .whereEqualTo("position_id", position_id) // 그냥 building id 말고 position id만
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {   // delete document
//
//                                db.collection(READINGS_TABLE).document(document.getId())
//                                        .delete()
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w("TAG", "Error deleting document", e);
//                                            }
//                                        });
//
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return 1;
//
//    }
//
//
////    public boolean deleteBuilding(String building_id) {
////
////        // not use
////
////        return true;
////
////    }
//
//    public ArrayList<String> getBuildings() {
//
//        CollectionReference ref = db.collection(AP_TABLE);
//        ArrayList<String> result = new ArrayList<String>();
//        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String building_id = document.getString("building_id");
//
//                        if(!result.contains(building_id))   // distinct한 building id 값을 얻기 위해
//                            result.add(building_id);
//                    }
//                }
//            }
//        });
//
//        return result;
//    }
//
//    public ArrayList<Router> getFriendlyWifis(String building_id) {
//
//        ArrayList<Router> result = new ArrayList<Router>();
//
//        db.collection(AP_TABLE)
//                .whereEqualTo("building_id", building_id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String ssid = document.getString("ssid");
//                                String mac_id = document.getString("mac_id");
//                                result.add(new Router(ssid, mac_id));
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return result;
//
//    }
//
//    public int deleteFriendlyWifis(String building_id) {
//        db.collection(AP_TABLE)
//                .whereEqualTo("building_id", building_id) // 그냥 building id 말고 position id만
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {   // delete document
//
//                                db.collection(READINGS_TABLE).document(document.getId())
//                                        .delete()
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w("TAG", "Error deleting document", e);
//                                            }
//                                        });
//
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return 1;
//
//    }
//
//    public boolean addFriendlyWifis(String building_id, ArrayList<Router> wifis) {
//
//        deleteFriendlyWifis(building_id);
//
//        for (int i = 0; i < wifis.size(); i++) {
//            Map<String, Object> data = new HashMap<>();
//            data.put("building_id", building_id);
//            data.put("ssid", wifis.get(i).getSSID());
//            data.put("mac_id", wifis.get(i).getBSSID());
//
//            db.collection(AP_TABLE)
//                    .add(data)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("TAG", "Error adding document", e);
//                        }
//                    });
//        }
//
//        return true;
//    }
//
//    public ArrayList<String> getPositions(String building_id) {
//
//        ArrayList<String> result = new ArrayList<String>();
//
//        db.collection(READINGS_TABLE)
//                .whereEqualTo("building_id", building_id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String position_id = document.getString("position_id");
//                                result.add(position_id);
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        return result;
//    }
//
//    public boolean addReadings(String building_id, PositionData positionData) {
//        Log.v("Just Before db : ", positionData.toString());
//        deleteReading(building_id, positionData.getName());
//
//        for (Map.Entry<String, Integer> e : positionData.getValues().entrySet()) {
//            Map<String, Object> data = new HashMap<>();
//            data.put("building_id", building_id);
//            data.put("position_id", positionData.getName());
//            data.put("ssid",positionData.routers.get(e.getKey()));
//            data.put("mac_id",e.getKey());
//            data.put("rssi", e.getValue());
//
//            db.collection(READINGS_TABLE)
//                    .add(data)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("TAG", "Error adding document", e);
//                        }
//                    });
//        }
//
//        return true;
//    }
//
//
//    public ArrayList<PositionData> getReadings(String building_id) {
//        HashMap<String, PositionData> positions = new HashMap<String, PositionData>();
//
//        db.collection(READINGS_TABLE)
//                .whereEqualTo("building_id", building_id)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String position_id = document.getString("position_id");
//                                Router router = new Router(document.getString("ssid"), document.getString("mac_id"));
//                                int rssi = Math.toIntExact(document.getLong("rssi"));
//                                if (positions.containsKey(position_id)) {
//                                    positions.get(position_id).addValue(router, rssi);
//                                }
//                                else {
//                                    PositionData positionData = new PositionData(position_id);
//                                    positionData.addValue(router, rssi);
//                                    positions.put(position_id, positionData);
//                                }
//                            }
//                        } else {
//                            Log.d("TAG", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//
//        ArrayList<PositionData> result = new ArrayList<PositionData>();
//        for (Map.Entry<String, PositionData> e : positions.entrySet())
//            result.add(e.getValue());
//
//        return result;
//
//    }
//}
