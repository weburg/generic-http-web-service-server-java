package com.weburg;

import com.logitech.gaming.LogiLED;

class ScratchLogitech {
    private static final int MIN_SLEEP = 5;

    public static void main(String[] args) throws InterruptedException {
        boolean isInit = LogiLED.LogiLedInit();

        LogiLED.LogiLedSaveCurrentLighting();

        LogiLED.LogiLedSetLighting(0, 0, 0); // percentages

        Thread.sleep(MIN_SLEEP); // Need to sleep between calls, otherwise state between calls isn't reliably read

        Thread.sleep(1000);

        System.out.println("Pulsing");
        LogiLED.LogiLedPulseLighting(0, 100, 0, LogiLED.LOGI_LED_DURATION_INFINITE, 20);
        Thread.sleep(7000);

        if (!LogiLED.LogiLedStopEffects()) {
            System.out.println("Failed to stop effect");
        }

        System.out.println("Flashing");
        boolean flashing = false;
        int tries = 0;
        while (!flashing) {
            tries++;
            System.out.println("Flashing try " + tries);
            flashing = LogiLED.LogiLedFlashLighting(100, 0, 0, LogiLED.LOGI_LED_DURATION_INFINITE, 200); // rgb, ms duration, interval
            if (tries > 50) {
                break;
            } else if (!flashing) {
                Thread.sleep(MIN_SLEEP); // Without this, it took 299 tries
            }
        }
        Thread.sleep(7000);

        if (!LogiLED.LogiLedStopEffects()) {
            System.out.println("Failed to stop effect");
        }

        Thread.sleep(1000);

        if (true) {
            LogiLED.LogiLedSetLighting(100, 0, 0); // percentages, rgb

            Thread.sleep(1500);

            LogiLED.LogiLedSetLighting(0, 100, 0); // percentages

            Thread.sleep(1500);

            LogiLED.LogiLedSetLighting(0, 0, 100); // percentages

            Thread.sleep(1500);

            LogiLED.LogiLedSetLighting(100, 0, 0); // percentages

            Thread.sleep(1500);

            LogiLED.LogiLedSetLighting(0, 100, 0); // percentages

            Thread.sleep(1500);

            LogiLED.LogiLedSetLighting(0, 0, 100); // percentages

            Thread.sleep(1500);
        }

        LogiLED.LogiLedRestoreLighting();
        Thread.sleep(200);
        LogiLED.LogiLedRestoreLighting();

        Thread.sleep(1000);

        LogiLED.LogiLedShutdown();
    }
}