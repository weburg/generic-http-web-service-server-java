package example;

import com.logitech.gaming.LogiLED;

public class ScratchLogitechSimple {
    private static final int MIN_SLEEP = 5;

    public static void main(String[] args) throws InterruptedException {
        boolean isInit = LogiLED.LogiLedInit();

        LogiLED.LogiLedSaveCurrentLighting();

        Thread.sleep(MIN_SLEEP); // Need to sleep between calls, otherwise state between calls isn't reliably read

        //LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 0, 0, 100, 0); // entire keyboard
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

    public static void setColor(int red, int green, int blue) throws InterruptedException {
        LogiLED.LogiLedShutdown();
        LogiLED.LogiLedInit();

        LogiLED.LogiLedSaveCurrentLighting();

        if (false) {
            //LogiLED.LogiLedSetLighting(red, green, blue);
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 0, red, green, blue); // entire keyboard
        } else {
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 1, red, green, blue); // left
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 2, red, green, blue); // center
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 3, red, green, blue); // right
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 4, red, green, blue); // arrows etc
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Keyboard, 5, red, green, blue); // numpad

            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Mouse, 0, red, green, blue); // DPI
            LogiLED.LogiLedSetLightingForTargetZone(LogiLED.DeviceType_Mouse, 1, red, green, blue); // logo
        }
    }

    public static void resetColor() throws InterruptedException {
        LogiLED.LogiLedRestoreLighting();
        LogiLED.LogiLedShutdown();
    }
}