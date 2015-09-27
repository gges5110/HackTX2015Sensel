package com.example.android.bluetoothchat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Gerry on 2015/9/27.
 */
public class SenselInputGroup {
    private static final String TAG = "SenselInputGroup";

    private HashMap<Integer, SenselInput> inputs;

    public SenselInputGroup(){
        inputs = new HashMap<Integer, SenselInput>();
    }

    public void addMessage(String s){
        SenselInput newInput = new SenselInput(s);
        if(newInput.isValid() && !inputs.containsKey(newInput.getContactID())) {
            inputs.put(newInput.getContactID(), newInput);
        }
    }

    public int size(){
        return inputs.size();
    }

    public SenselInput getSenselInputByContactID(int contactID){
        return inputs.get(contactID);
    }

    public boolean isEnd(){
        for(Integer contactID : inputs.keySet())
            if (SenselInput.Event.END.equals(inputs.get(contactID).getEvent()))
                return true;
        return false;
    }

    public ArrayList<Integer> getContactIDs(){
        ArrayList<Integer> contactIDs = new ArrayList<Integer>();
        contactIDs.addAll(inputs.keySet());
        Collections.sort(contactIDs);
        return contactIDs;
    }

}