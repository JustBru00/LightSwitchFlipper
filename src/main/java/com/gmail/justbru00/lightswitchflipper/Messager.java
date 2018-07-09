package com.gmail.justbru00.lightswitchflipper;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;

public class Messager {
	
	private static ColoredPrinter red = new ColoredPrinter.Builder(1, false).foreground(FColor.RED).build();

	public static void debug(String msg) {
		if (LightSwitchFlipperMain.DEBUG) {
			System.out.println(getLogTimeStamp() + " [DEBUG] " + msg + "");
		}
	}
	
	public static void info(String msg) {
		System.out.println(getLogTimeStamp() + " [INFO] " + msg + "");
	}
	
	public static void warn(String msg) {
		red.print(getLogTimeStamp() + " [WARNING] " + msg + "\n", Attribute.NONE, FColor.RED, BColor.BLACK);
		red.clear();
	}
	
	public static void critical(String msg) {
		red.print(getLogTimeStamp() + " [CRITICAL] " + msg + "\n", Attribute.NONE, FColor.RED, BColor.BLACK);
		red.clear();
	}
	
	public static String getLogTimeStamp() {
		return "[" + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()) + "]";
	}
	
}
