package sensorteam10.gachon.wi_fi_based_indoor_positioning_app;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PositionData implements Serializable {
	public static final int MAX_DISTANCE=99999999;
	private String name;
	public static final int MINIMUM_COMMON_ROUTERS=1;
	public HashMap<String, Integer> values;
    public HashMap<String,String> routers;
	public PositionData(String name) {
		// TODO Auto-generated constructor stub
		this.name=name;
		values = new HashMap<String, Integer>();
        routers = new HashMap<String, String>();

	}
	public void addValue(Router router,int strength){

		values.put(router.getBSSID(), strength);
        routers.put(router.getBSSID(),router.getSSID());

	}
	public String getName() {
		return name;
	}
	public String toString() {
		String result="";
		result+=name+"\n";
		for(Map.Entry<String, Integer> e: this.values.entrySet())
				 result+=routers.get(e.getKey())+" : "+e.getValue().toString()+"\n";
		
		return result; 	
		
	}
	public HashMap<String, Integer> getValues() {
		return values;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public int uDistance(PositionData arg, ArrayList<Router> friendlyWifis){
		int sum=0;
		int count=0;

		List<Map.Entry<String, Integer>> entryList = new LinkedList<>(values.entrySet());
		entryList.sort((o1, o2) -> values.get(o2.getKey()) - values.get(o1.getKey()));

		List<Map.Entry<String, Integer>> entryList2 = new LinkedList<>(arg.values.entrySet());
		entryList2.sort((o1, o2) -> arg.values.get(o2.getKey()) - arg.values.get(o1.getKey()));


		Log.v("LARGEST 5", String.valueOf(entryList2));

		for(Map.Entry<String, Integer> e: entryList2){
			if(count == 5)
				break;

			int v;
			int v2;
			//Log.v("Key : ",arg.values.get(e.getKey()).toString());
			if(values.containsKey(e.getKey()))
			{
				v= values.get(e.getKey());
				v2 = e.getValue();
				sum+=Math.pow((v - v2),2);
				count++;

				Log.v("LARGEST 5",  e.getKey() + e.getValue());
			}
			else{
				sum+=10000;
				count++;
			}
		}
		if(count<MINIMUM_COMMON_ROUTERS){
			sum=MAX_DISTANCE;
		}

//		for(Map.Entry<String, Integer> e: entryList){
//			if(count == 5)
//				break;
//
//			int v;
//			//Log.v("Key : ",arg.values.get(e.getKey()).toString());
//			if(isFriendlyWifi(friendlyWifis,e.getKey()) && arg.values.containsKey(e.getKey()))
//			{
//				v=arg.values.get(e.getKey());
//				sum+=Math.pow((v-e.getValue()),2);
//				count++;
//
//				Log.v("LARGEST 5",  e.getKey() + e.getValue());
//			}
//		}
//		if(count<MINIMUM_COMMON_ROUTERS){
//			sum=MAX_DISTANCE;
//		}

		return sum;
	}

    private boolean isFriendlyWifi(ArrayList<Router> wifis,String bssid){
        for(int i=0;i<wifis.size();i++){
            if(wifis.get(i).getBSSID().equals(bssid))
                return true;

        }
        return false;

    }
	
}
