package com.example.android.bluetoothchat;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Created by Gerry on 2015/9/27.
 */
public class Gesture {
    private static final String TAG = "Gesture";

    private static final int short_threshold = 2;
    private static final int long_threshold = 60;

//    private Handler gestureHandle;
    private LinkedList<SenselInputGroup> queue;
    private BluetoothChatFragment handler;

    private Direction dir;
    private NumFingers numFingers;
    private boolean longPress;
//    private String messages;

    public enum Direction {UP, DOWN, LEFT, RIGHT, INVALID};
    public enum NumFingers {ONE, TWO, THREE, FOUR, FIVE, INVALID};

    public Gesture(BluetoothChatFragment h){
//    public Gesture(Handler handler) {
//        gestureHandle = handler;
        handler = h;
        queue = new LinkedList<SenselInputGroup>();
        queue.addLast(new SenselInputGroup());
//        messages = "";
        dir = Direction.INVALID;
        numFingers = NumFingers.INVALID;
        longPress = false;
    }

    public void add(String s){
        if("****".equals(s) && queue.getLast().size() > 0) {
            //TODO detect gesture here
            if(queue.getLast().isEnd()) {
                Log.v(TAG, "Found end!!");
                findGesture();
            }
            queue.addLast(new SenselInputGroup());
        }
        else {
            queue.getLast().addMessage(s);
        }
    }

    private int  findNumFingers(){
        HashMap<Integer,Integer> countIDs = new HashMap<>();
        for(SenselInputGroup inputGroup : queue){
            if (!countIDs.containsKey(inputGroup.size()))
                countIDs.put(inputGroup.size(), 1);
            else
                countIDs.put(inputGroup.size(), countIDs.get(inputGroup.size())+1);
        }
        int max_occurrence = 0;
        int max_occurrence_key = -1;
        for(Integer key : countIDs.keySet()){
            if(countIDs.get(key) > max_occurrence){
                max_occurrence = countIDs.get(key);
                max_occurrence_key = key;
            }
        }

        switch(max_occurrence_key){
            case 1:
                numFingers = NumFingers.ONE;
                break;
            case 2:
                numFingers = NumFingers.TWO;
                break;
            case 3:
                numFingers = NumFingers.THREE;
                break;
            case 4:
                numFingers = NumFingers.FOUR;
                break;
            case 5:
                numFingers = NumFingers.FIVE;
                break;
            default:
                numFingers = NumFingers.INVALID;
        }
        return max_occurrence_key;
    }

    private void findDirection(int numFingers) {
        SenselInputGroup first, last;
        do {
            first = queue.removeFirst();
        } while (first.size() != numFingers);

        try {
            do {
                //TODO could crash when no more element is left
                last = queue.removeLast();
            } while (last.size() != numFingers);
        }catch(NoSuchElementException e){
            dir = Direction.INVALID;
            return;
        }

        // hopefully the contactIDs from "first" and "last" are the same...
        double sumXDiff = 0, sumYDiff = 0;
        ArrayList<Integer> intersect = new ArrayList<>();
        intersect.addAll(first.getContactIDs());
        intersect.retainAll(last.getContactIDs());
        for (int contactID : intersect) {
            double xDiff = last.getSenselInputByContactID(contactID).getX() - first.getSenselInputByContactID(contactID).getX();
            double yDiff = last.getSenselInputByContactID(contactID).getY() - first.getSenselInputByContactID(contactID).getY();
            sumXDiff += xDiff;
            sumYDiff += yDiff;
        }

        if (sumXDiff == 0){
            dir = Direction.INVALID;
            return;
        }
        double arctan = Math.atan(sumYDiff/sumXDiff);
        Log.v(TAG, "xDiff " + sumXDiff);
        Log.v(TAG, "yDiff" + sumYDiff);
        Log.v(TAG, "arctan " + arctan);
        if(arctan >= Math.PI/4){ // top or bottom
            if (sumYDiff >= 0 && sumXDiff >= 0)
                dir = Direction.DOWN;
            else
                dir = Direction.UP;
        }
        else if (arctan >= -Math.PI/4 && arctan < Math.PI/4){
            if(sumXDiff > 0)
                dir = Direction.RIGHT;
            else
                dir = Direction.LEFT;
        }else {
            if(sumYDiff >=0 && sumXDiff < 0)
                dir = Direction.DOWN;
            else
                dir = Direction.UP;
        }
    }

    private void findGesture(){
        if(queue.size() < short_threshold) {
            Log.v(TAG, "gesture too short, discarded");
            return;
        }
        int fingerCount = findNumFingers();
        Log.v(TAG, "finger count " + numFingers);

        if (queue.size() > long_threshold) {
            Log.v(TAG, "long press, queue size " + queue.size());
            longPress = true;
        }

        //detect direction
        findDirection(fingerCount);
        Log.v(TAG, "direction " + dir);

        if(!NumFingers.ONE.equals(getNumFingers()))
            handler.gestureDetected(isLongPress(), dir, numFingers);
        queue.clear();
    }

    public Direction getDir(){
        return dir;
    }

    public NumFingers getNumFingers(){
        return numFingers;
    }

    public boolean isLongPress(){
        return longPress;
    }
}
