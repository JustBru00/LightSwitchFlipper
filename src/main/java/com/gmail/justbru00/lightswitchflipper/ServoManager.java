package com.gmail.justbru00.lightswitchflipper;

import java.util.ArrayList;

import com.gmail.justbru00.lightswitchflipper.hardware.PCA9685;

public class ServoManager {

	public static final Float SG90_DEFAULT_UP = 2f;
	public static final Float SG90_DEFAULT_DOWN = 1f;
	public static final Float SG90_DEFAULT_CENTER = 1.5f;
	
	private static PCA9685 servoDriver;
	
	public static PCA9685 getServoDriver() {
		return servoDriver;
	}
	
	private static ArrayList<Servo> allServos = new ArrayList<Servo>();
	
	
}
