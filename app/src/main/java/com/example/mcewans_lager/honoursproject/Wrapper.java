package com.example.mcewans_lager.honoursproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mcewans_lager on 26/02/16.
 */
public class Wrapper implements Serializable {

    private ArrayList<String> locations;

    public Wrapper(ArrayList<String> wList) {
        this.locations = wList;
    }

    public ArrayList<String> getNames() {
        return this.locations;
    }
}
