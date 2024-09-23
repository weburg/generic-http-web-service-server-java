package com.weburg;

import com.logitech.gaming.LogiLED;

public class ScratchLogitechSimple {
	private static final int MIN_SLEEP = 5;

	public static void main(String[] args) throws InterruptedException {
		boolean isInit = LogiLED.LogiLedInit();

		LogiLED.LogiLedSaveCurrentLighting();

		Thread.sleep(MIN_SLEEP); // Need to sleep between calls, otherwise state between calls isn't reliably read

		//LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 1, 0, 100, 0); // entire keyboard
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 1, 0, 100, 0); // left
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 2, 0, 80, 20); // center
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 3, 0, 60, 40); // right
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 4, 0, 0, 100); // arrows etc
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 5, 100, 0, 0); // numpad

		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Mouse, 0, 100, 0, 0); // DPI
		LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Mouse, 1, 0, 100, 0); // logo

		Thread.sleep(5000);

		LogiLED.LogiLedRestoreLighting();

		Thread.sleep(MIN_SLEEP);

		LogiLED.LogiLedShutdown();
	}
}