package com.example.wifimanager;

public class Realtime {
    public String building_id ="AI";
    public String mac_id ;
    public String position_id;
    public String ssid ;
    public double rssi;
    public Realtime(){}
    public Realtime(String p, String mac, String ss,double r ) {
        this.mac_id = mac;
        this.ssid=ss;
        this.position_id=p;
        this.rssi=r;
    }


    public String getBuilding_id() {
        return building_id;
    }

    public String getMac_id() {
        return mac_id;
    }

    public String getPosition_id() {
        return position_id;
    }

    public String getSsid() {
        return ssid;
    }

    public double getRssi() {
        return rssi;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setRssi(double rssi) {
        this.rssi = rssi;
    }
}
