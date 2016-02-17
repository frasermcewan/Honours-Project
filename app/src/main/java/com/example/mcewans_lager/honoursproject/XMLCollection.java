package com.example.mcewans_lager.honoursproject;

/**
 * Created by mcewans_lager on 17/02/16.
 */
public class XMLCollection {
    int temp = 0;
    int windspeed = 0;
    String Location = null;
    String Precip = null;




   public void setLocation(String loc) {
       Location = loc;
   }

   public void setTemp(int Temps) {
       temp = Temps;
   }


   public void setPrecip(String p) {
       Precip = p;
   }

    public void setWindspeed(int w) {
        windspeed = w;
    }







}
