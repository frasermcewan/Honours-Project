package com.example.mcewans_lager.honoursproject;




public class Signatures {

    private int _id;
    private double Time;
    private String _locationName;
    private String _lat;
    private String _lon;
    private String _Wifi;


    public Signatures() {

    }

    public Signatures(String locationName){
        this._locationName = locationName;

    }

    public void setLocationName(String locationName) {
        this._locationName = locationName;
    }


    public void setTime (Double timeIn) {
        this.Time = timeIn;
    }

    public void setID(int id) {
        this._id = id;

    }

    public void setLat(String GPS) {
        this._lat = GPS;
    }


    public void setWIFI(String Wifi) {
        this._Wifi = Wifi;
    }

    public void setLon(String Long) { this._lon = Long;}

    public String getLocationName() {
        return _locationName;
    }

    public int getID(){
        return _id;
    }

    public String getLat () {
        return _lat;
    }

    public String getWifi () {
        return _Wifi;
    }

    public String get_lon() { return _lon;}

    public Double getTime() { return Time;}

}