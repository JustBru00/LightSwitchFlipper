package com.gmail.justbru00.lightswitchflipper.hardware;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * Most of the code in this class was found at:
 * https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/I2C.SPI/src/i2c/servo/pwm/PCA9685.java
 * @author Oliver LeDiouris
 *
 * Justin Brubaker has added methods to help with this project.
 * Methods created by Justin Brubaker will be marked with a author tag.
 */
public class PCA9685 {
	public final static int PCA9685_ADDRESS = 0x40;

	public final static int SUBADR1 = 0x02;
	public final static int SUBADR2 = 0x03;
	public final static int SUBADR3 = 0x04;
	public final static int MODE1 = 0x00;
	public final static int PRESCALE = 0xFE;
	public final static int LED0_ON_L = 0x06;
	public final static int LED0_ON_H = 0x07;
	public final static int LED0_OFF_L = 0x08;
	public final static int LED0_OFF_H = 0x09;
	public final static int ALL_LED_ON_L = 0xFA;
	public final static int ALL_LED_ON_H = 0xFB;
	public final static int ALL_LED_OFF_L = 0xFC;
	public final static int ALL_LED_OFF_H = 0xFD;

	private static boolean verbose = true;
	private int freq = 60;

	private I2CBus bus;
	private I2CDevice servoDriver;

	public PCA9685() throws I2CFactory.UnsupportedBusNumberException {
		this(PCA9685_ADDRESS); // 0x40 obtained through sudo i2cdetect -y 1
	}

	public PCA9685(int address) throws I2CFactory.UnsupportedBusNumberException {
		try {
			// Get I2C bus
			bus = I2CFactory.getInstance(I2CBus.BUS_1); // Depends on the RasPI version
			if (verbose) {
				System.out.println("Connected to bus. OK.");
			}

			// Get the device itself
			servoDriver = bus.getDevice(address);
			if (verbose) {
				System.out.println("Connected to device. OK.");
			}
			// Reseting
			servoDriver.write(MODE1, (byte) 0x00);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * @param freq 40..1000
	 */
	public void setPWMFreq(int freq) {
		this.freq = freq;
		float preScaleVal = 25_000_000.0f; // 25MHz
		preScaleVal /= 4_096.0;           // 4096: 12-bit
		preScaleVal /= freq;
		preScaleVal -= 1.0;
		if (verbose) {
			System.out.println("Setting PWM frequency to " + freq + " Hz");
			System.out.println("Estimated pre-scale: " + preScaleVal);
		}
		double preScale = Math.floor(preScaleVal + 0.5);
		if (verbose) {
			System.out.println("Final pre-scale: " + preScale);
		}

		try {
			byte oldmode = (byte) servoDriver.read(MODE1);
			byte newmode = (byte) ((oldmode & 0x7F) | 0x10); // sleep
			servoDriver.write(MODE1, newmode);               // go to sleep
			servoDriver.write(PRESCALE, (byte) (Math.floor(preScale)));
			servoDriver.write(MODE1, oldmode);
			delay(5);
			servoDriver.write(MODE1, (byte) (oldmode | 0x80));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	/**
	 * @param channel 0..15
	 * @param on      0..4095 (2^12 positions)
	 * @param off     0..4095 (2^12 positions)
	 */
	public void setPWM(int channel, int on, int off) throws IllegalArgumentException {
		if (channel < 0 || channel > 15) {
			throw new IllegalArgumentException("Channel must be in [0, 15]");
		}
		if (on < 0 || on > 4_095) {
			throw new IllegalArgumentException("On must be in [0, 4095]");
		}
		if (off < 0 || off > 4_095) {
			throw new IllegalArgumentException("Off must be in [0, 4095]");
		}
		if (on > off) {
			throw new IllegalArgumentException("Off must be greater than On");
		}
		try {
			servoDriver.write(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
			servoDriver.write(LED0_ON_H + 4 * channel, (byte) (on >> 8));
			servoDriver.write(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
			servoDriver.write(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * @param channel 0..15
	 * @param pulseMS in ms.
	 */
	public void setServoPulse(int channel, float pulseMS) {
		double pulseLength = 1_000_000; // 1s = 1,000,000 us per pulse. "us" is to be read "micro (mu) sec".
		pulseLength /= this.freq;  // 40..1000 Hz
		pulseLength /= 4_096;       // 12 bits of resolution
		int pulse = (int) (pulseMS * 1_000);
		pulse /= pulseLength;
		if (verbose) {
			System.out.println(pulseLength + " us per bit, pulse:" + pulse);
		}
		this.setPWM(channel, 0, pulse);
	}
	
	/**
	 * From: https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/common-utils/src/java/utils/TimeUtil.java
	 * @param howMuch in ms.
	 */
	public static void delay(long howMuch) {
		try {
			Thread.sleep(howMuch);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * From: https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/common-utils/src/java/utils/TimeUtil.java
	 * @param ms
	 * @param nano
	 */
	public static void delay(long ms, int nano) {
		try {
			Thread.sleep(ms, nano);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * From: https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/common-utils/src/java/utils/TimeUtil.java
	 * @param sec in seconds
	 */
	public static void delay(float sec) {
		delay(Math.round(sec * 1_000));
	}
}
