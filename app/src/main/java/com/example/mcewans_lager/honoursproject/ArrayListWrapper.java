package com.example.mcewans_lager.honoursproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fraser McEwan on 26/02/16.
 *
 * This class acts as a Serializable wrapper in order to be able to pass information
 * stored in Arraylists to other services. This is used to pass WIFI data and Weather data
 * from services to the Main Service for contextual analysis
 *
 */
public class ArrayListWrapper implements Serializable {

    private ArrayList<String> items;

    public ArrayListWrapper(ArrayList<String> wList) {
        this.items = wList;
    }

    public ArrayList<String> getItems() {
        return this.items;
    }
}
