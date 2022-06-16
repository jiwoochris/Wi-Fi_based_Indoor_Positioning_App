package com.example.wifimanager;

public class FireStoreDB {

        public String building_id ="AI";
        public String mac_id="" ;
        public String position_id="";
        public String ssid="" ;
        public int rssi=0;
        public int i=0;
        public FireStoreDB(){}
        public FireStoreDB(String p, String mac, String ss,int r ) {
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

        public int getRssi() {
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

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}


