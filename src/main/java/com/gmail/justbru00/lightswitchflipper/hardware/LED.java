package com.gmail.justbru00.lightswitchflipper.hardware;

import com.gmail.justbru00.lightswitchflipper.LightSwitchFlipperMain;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

/**
 * A class for controlling LED outputs easily.
 * 
 * @author Justin Brubaker
 *
 */
public class LED {

	private LEDState currentState = LEDState.OFF;
	private Pin pin = null;
	private String name = null;
	private GpioPinDigitalOutput thisLED;
	private final GpioController gpio = GpioFactory.getInstance();
	private boolean runTimer = false;
	private boolean on = false;
	private boolean NO_FLASHING_EVER = false;

	/**
	 * This constructor is used to create a LED object to control a LED connected to
	 * the given pin.
	 * 
	 * @param _pin
	 * @param _name
	 */
	public LED(Pin _pin, String _name) {
		pin = _pin;
		name = _name;
		thisLED = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
		update();
		startTimer();
	}
	
	/**
	 * This constuctor is used to create a LED object to control a LED connected to
	 * the given pin.
	 * 
	 * @param _pin
	 * @param _name
	 */
	public LED(Pin _pin, String _name, boolean neverAllowFlashing) {
		pin = _pin;
		name = _name;
		thisLED = gpio.provisionDigitalOutputPin(pin, name, PinState.LOW);
		neverAllowFlashing = NO_FLASHING_EVER;
		update();
		startTimer();
	}
	
	/**
	 * Used for {@link LEDSynchronizer}
	 * @param value
	 * @deprecated
	 */
	public void setOn(boolean value) {
		on = value;
	}
	
	/**
	 * Used for {@link LEDSynchronizer}
	 * @return
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * This method sets the current output for the LED.
	 * 
	 * @param state
	 *            The state for the LED to display.
	 */
	public void setState(LEDState state) {
		currentState = state;
		update();
		if (state == LEDState.ON) {
			on = true;
		} else if (state == LEDState.OFF) {
			on = false;
		}
	}
	
	public LEDState getState() {
		return currentState;
	}

	/**
	 * This will force an update of the LED.
	 */
	public void update() {
		if (currentState.equals(LEDState.OFF)) {
			thisLED.low();
		} else if (currentState.equals(LEDState.ON)) {
			thisLED.high();
		}
	}
	/**
	 * Returns the pin controlling this LED
	 * @return
	 */
	public GpioPinDigitalOutput getPin() {
		return thisLED;
	}

	/**
	 * Starts the timer for the LED to flash
	 */
	public void startTimer() {
		if (runTimer) {
			// Timer already running
			// Fail silently
			return;
		}
		runTimer = true;
		new Thread(new Runnable() {

			public void run() {

				boolean on = false;
				byte counter = 0;

				while (runTimer) {
					if (currentState.equals(LEDState.FLASHING_FAST) && !NO_FLASHING_EVER) {
						if (on) {
							thisLED.low();
							on = false;
						} else {
							thisLED.high();
							on = true;
						}
					} else if (currentState.equals(LEDState.FLASHING_SLOW) && !NO_FLASHING_EVER) {
						if (counter == 1) {
							if (on) {
								thisLED.low();
								on = false;
							} else {
								thisLED.high();
								on = true;
							}
							counter = 0;
						} else {
							counter++;
						}
					}

					synchronized (this) {
						try {
							this.wait(LightSwitchFlipperMain.LED_TIMER_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	/**
	 * Used to stop the LED objects internal timer for flashing.
	 */
	public void stopTimer() {
		runTimer = false;
	}

}