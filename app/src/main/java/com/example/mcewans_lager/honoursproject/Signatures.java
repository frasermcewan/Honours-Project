package com.example.mcewans_lager.honoursproject;




public class Signatures {

    private int _id;
    private String _locationName;
    private String _GPS;
    private String _Wifi;


    public Signatures() {

    }

    public Signatures(String locationName){
        this._locationName = locationName;

    }

    public void setLocationName(String locationName) {
        this._locationName = locationName;
    }

    public void setID(int id) {
        this._id = id;

    }

    public void setGPS(String GPS) {
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

    public String getGPS () {
        return _GPS;
    }

    public String getWifi () {
        return _Wifi;
    }
}