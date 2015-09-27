package com.example.android.bluetoothchat;

import android.os.Handler;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;

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
    private Fingers fingers;
//    private String messages;

    public enum Direction {UP, DOWN, LEFT, RIGHT, INVALID};
    public enum Fingers {TWO, THREE, FOUR, FIVE, INVALID};

    public Gesture(BluetoothChatFragment h){
//    public Gesture(Handler handler) {
//        gestureHandle = handler;
        handler = h;
        queue = new LinkedList<SenselInputGroup>();
        queue.addLast(new SenselInputGroup());
//        messages = "";
        dir = Direction.INVALID;
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
            case 2:
                fingers = Fingers.TWO;
                break;
            case 3:
                fingers = Fingers.THREE;
                break;
            case 4:
                fingers = Fingers.FOUR;
                break;
            case 5:
                fingers = Fingers.FIVE;
                break;
            default:
                fingers = Fingers.INVALID;
        }
        return max_occurrence_key;
    }

    private void findDirection(int numFingers) {
        SenselInputGroup first, last;
        do {
            first = queue.removeFirst();
        } while (first.size() != numFingers);

        do {
            last = queue.removeLast();
        } while (last.size() != numFingers);

        // hopefully the contactIDs from "first" and "last" are the same...
        double sumXDiff= 0, sumYDiff=0;
        for (int contactID : first.getContactIDs()) {
            double xDiff = last.getSenselInputByContactID(contactID).getX() - first.getSenselInputByContactID(contactID).getX();
            double yDiff = last.getSenselInputByContactID(contactID).getY() - first.getSenselInputByContactID(contactID).getY();
            sumXDiff += xDiff;
            sumYDiff += yDiff;
        }

        double arctan = Math.atan(sumYDiff/sumXDiff);
        if(arctan >= Math.PI/2){ // top or bottom
            if(sumYDiff >=0)
                dir = Direction.UP;
            else
                dir = Direction.DOWN;
        }
        else if (arctan >= -Math.PI/2 && arctan < Math.PI/2){
            if(sumXDiff > 0)
                dir = Direction.RIGHT;
            else
                dir = Direction.LEFT;
        }else {
            if (sumYDiff >= 0)
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
        Log.v(TAG, "finger count " + fingers);

        if (queue.size() > long_threshold)
            Log.v(TAG, "long press, queue size " + queue.size());

        //detect direction
        findDirection(fingerCount);
        Log.v(TAG, "direction " + dir);

        handler.gestureDetected();
        queue.clear();
    }

}
