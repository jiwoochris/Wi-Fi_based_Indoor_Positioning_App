package com.example.wifimanager;

import java.util.Comparator;

public class RealtimeComparator implements Comparator<Realtime> {


    @Override
    public int compare(Realtime r, Realtime t) {
       if(r.rssi > t.rssi){
           return 1;
       }else if (r.rssi < t.rssi){
           return -1;
       }
        return 0;
    }

    @Override
    public Comparator<Realtime> reversed() {
        return Comparator.super.reversed();
    }


}
