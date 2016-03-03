package com.example.mcewans_lager.honoursproject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mcewans_lager on 26/02/16.
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
