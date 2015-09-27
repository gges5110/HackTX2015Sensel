package com.example.android.bluetoothchat;

public class SenselInput {

	private int contactID;
	private Event event;
	private float coordinateX;
	private float coordinateY;
	private int force;
	private float major;
	private float minor;
	private float orientation;
	private boolean valid;

	public enum Event {START, MOVE, END, INVALID}

	public SenselInput(String s){
		String[] attr = s.split(", ");
		try{
			if(attr.length == 8 &&
				parseContactID(attr[0]) && 
				parseEvent(attr[1]) &&
				parseCoordinateX(attr[2]) &&
				parseCoordinateY(attr[3]) &&
				parseForce(attr[4]) &&
				parseMajor(attr[5]) &&
				parseMinor(attr[6]) &&
				parseOrientation(attr[7]))
				valid = true;
			else
				valid = false;
		}catch(Exception e){
			e.printStackTrace();
			valid = false;
		}
	}
	
	private boolean parseContactID(String s){
		if(s.indexOf("Contact ID ") == 0){
			contactID=Integer.parseInt(s.substring("Contact ID ".length()));
			return true;
		}

		return false;
	}

	private boolean parseEvent(String s){
		if(s.indexOf("event=") == 0){
			String parse_event = s.substring("event=".length());
			if("start".equals(parse_event))
				event = Event.START;
			else if("move".equals(parse_event))
				event = Event.MOVE;
			else if ("end".equals(parse_event))
				event = Event.END;
			else
				event = Event.INVALID;
			
			return true;
		}
		return false;
	}
	
	private boolean parseCoordinateX(String s){
		if(s.indexOf("mm coord: (") == 0){
			coordinateX = Float.parseFloat(s.substring("mm coord: (".length()));
			return true;
		}
		return false;
	}
	
	private boolean parseCoordinateY(String s){
		//TODO check length of s
		coordinateY = Float.parseFloat(s.substring(0, s.length()-2));
		return true;
	}
	
	private boolean parseForce(String s){
		if(s.indexOf("force=") == 0){
			force = Integer.parseInt(s.substring("force=".length()));
			return true;
		}
		return false; 
	}
	
	private boolean parseMajor(String s){
		if(s.indexOf("major=") == 0){
			major = Float.parseFloat(s.substring("major=".length()));
			return true;
		}
		return false;
	}

	private boolean parseMinor(String s){
		if(s.indexOf("minor=") == 0){
			minor = Float.parseFloat(s.substring("minor=".length()));
			return true;
		}
		return false;
	}
	
	private boolean parseOrientation(String s){
		if(s.indexOf("orientation=") == 0){
			orientation = Float.parseFloat(s.substring("orientation=".length()));
			return true;
		}
		return false;
	}

	public boolean isValid(){
		return valid;
	}

	public int getContactID() {
		return contactID;
	}

	public Event getEvent() {
		return event;
	}

	public float getX() {
		return coordinateX;
	}

	public float getY() {
		return coordinateY;
	}

	public int getForce() {
		return force;
	}

	public float getMajor() {
		return major;
	}

	public float getMinor() {
		return minor;
	}

	public float getOrientation() {
		return orientation;
	}
}
