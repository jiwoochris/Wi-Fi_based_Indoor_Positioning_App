package com.example.wifimanager;

import java.util.Comparator;

public class FireStoreComparator implements Comparator<FireStoreDB> {

    @Override
    public int compare(FireStoreDB r, FireStoreDB t) {
        if(r.i > t.i){
            return 1;
        }else if (r.i < t.i){
            return -1;
        }
        return 0;
    }
}
