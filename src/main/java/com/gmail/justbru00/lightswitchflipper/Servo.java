package com.gmail.justbru00.lightswitchflipper;
/**
 * Represents a single servo.
 * @author justb
 *
 */
public class Servo {

	// 90
	private Float upPulse = ServoManager.SG90_DEFAULT_UP;
	// 0
	private Float centerPulse = ServoManager.SG90_DEFAULT_CENTER;
	// -90
	private Float downPulse = ServoManager.SG90_DEFAULT_DOWN;
	
	// 0-15
	private int channel;
	
	
	public Servo(int _channel) {
		channel = _channel;
	}	
	
	/**
	 * Sets the servo to the given position.
	 * Respects any adjustments made to the pulse for this servo.
	 * @param sp
	 */
	public void setPosition(ServoPosition sp) {
		if (sp.equals(ServoPosition.UP)) {
			ServoManager.getServoDriver().setServoPulse(channel, upPulse);
		} else if (sp.equals(ServoPosition.CENTER)) {
			ServoManager.getServoDriver().setServoPulse(channel, centerPulse);
		} else if (sp.equals(ServoPosition.DOWN)) {
			ServoManager.getServoDriver().setServoPulse(channel, downPulse);
		}
	}	
	
	public Float getUpPulse() {
		return upPulse;
	}

	public void setUpPulse(Float upPulse) {
		this.upPulse = upPulse;
	}

	public Float getCenterPulse() {
		return centerPulse;
	}

	public void setCenterPulse(Float centerPulse) {
		this.centerPulse = centerPulse;
	}

	public Float getDownPulse() {
		return downPulse;
	}

	public void setDownPulse(Float downPulse) {
		this.downPulse = downPulse;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}	
}
