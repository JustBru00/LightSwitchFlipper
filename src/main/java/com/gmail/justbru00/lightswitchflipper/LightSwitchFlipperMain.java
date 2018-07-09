package com.gmail.justbru00.lightswitchflipper;

import java.util.Scanner;

import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.Console;

public class LightSwitchFlipperMain {

	public static final int LED_TIMER_INTERVAL = 200;
	public static LED STATUS = new LED(RaspiPin.GPIO_04, "Status LED", false);
	public static boolean DEBUG = true;
	public static final Console CONSOLE = new Console();
	public static boolean RUNNING = true;

	public static void main(String[] args) {
		
		CONSOLE.title("<-- LightSwitchFlipper -->");
		CONSOLE.box("LightSwitchFlipper is Copyright 2018 Justin Brubaker.",
				"LightSwitchFlipper uses the PI4J Project. The PI4J project is licensed under the LGPLv3. A copy of this license can be found at http://pi4j.com/license.html.");
		CONSOLE.box("Type 'stop' to stop the program");
		
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("-selftest")) {
				servoTest();
			}
		}
		
		new Thread(() -> {
			Scanner input = new Scanner(System.in);
			while (RUNNING) {				
				String txt = input.next();

				if (txt.equalsIgnoreCase("stop")) {
					RUNNING = false;
					Messager.info("Received stop request. Program should stop in ~1 second.");
				} else if (txt.equalsIgnoreCase("SERVOSUP")) {
					// TODO All servos up
					Messager.info("DONE SERVOSUP");
				} // TODO add more commands

				input.nextLine();				
			}
			input.close();
		}).start();
		
		// Set the status LED to flashing
		STATUS.setState(LEDState.FLASHING_FAST);
		Messager.info("Status LED has been set to FLASHING_FAST");
		
		// Center all servos
		// TODO All servos to center
		Messager.info("Servos are now CENTERED");
		
		// Start listening for clients
		Messager.info("Starting NetworkServerManager");
		// Prevent thread blocking
		new Thread(() -> {
			NetworkServerManager.startServer();
		}).start();
		
		
		
		while (RUNNING) {						
			// WAIT FOR SHUTDOWN
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// TODO Servos to CENTER
		STATUS.setState(LEDState.OFF);
		
		STATUS.stopTimer();
		NetworkServerManager.closeServer();
		// PROGRAM DONE
	}
	
	public static void servoTest() {
		Messager.info("Starting SERVO TEST");
		
		// TODO Test UP and DOWN for each servo
		
		// TODO Test ALLUP and ALLDOWN
		
		Messager.info("FINISHED GLOBE TEST");
	}
	
}
