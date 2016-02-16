package com.example.mcewans_lager.honoursproject;

import com.google.android.gms.maps.model.LatLng;




public class Locations {

    private int _id;
    private String _locationName;
    private LatLng _GPS;
    private String _Wifi;


    public Locations() {

    }

    public Locations(String locationName){
        this._locationName = locationName;

    }

    public void setLocationName(String locationName) {
        this._locationName = locationName;
    }

    public void setID(int id) {
        this._id = id;

    }

    public void setGPS(LatLng GPS) {
        this._GPS = GPS;
    }

    public void setWIFI(String Wifi) {
        this._Wifi = Wifi;
    }

    public String getLocationName() {
        return _locationName;
    }

    public int getID(){
        return _id;
    }

    public LatLng getGPS () {
        return _GPS;
    }

    public String getWifi () {
        return _Wifi;
    }
}
