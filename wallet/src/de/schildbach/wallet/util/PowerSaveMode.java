/*
 * Copyright 2016-2015 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.util;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

/**
 * Utility class for checking power save mode status on Android devices.
 * Enterprise-grade power management for optimal battery usage.
 *
 * @author Andreas Schildbach
 * @author MicroGuy (Goldcoin enterprise enhancements)
 */
public final class PowerSaveMode {

    /**
     * Checks if the device is in power save mode.
     * Enhanced for enterprise-grade battery optimization.
     *
     * @param context the application context
     * @return true if power save mode is enabled, false otherwise
     */
    public static boolean isEnabled(final Context context) {
        final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (powerManager == null) {
            return false; // Conservative approach - assume not in power save if we can't check
        }

        // Check power save mode (Android 5.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (powerManager.isPowerSaveMode()) {
                return true;
            }
        }

        // Enterprise enhancement: Also check for low power mode (Android 6.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if device is in doze mode or app standby
            if (!powerManager.isIgnoringBatteryOptimizations(context.getPackageName())) {
                // App is subject to battery optimizations - be conservative
                return powerManager.isDeviceIdleMode();
            }
        }

        return false;
    }

    /**
     * Enterprise enhancement: Check if device is in any battery optimization mode
     * that might affect blockchain synchronization performance.
     */
    public static boolean isBatteryOptimizationActive(final Context context) {
        final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (powerManager == null) {
            return true; // Conservative approach
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if the app is whitelisted from battery optimizations
            return !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }

        return false;
    }

    /**
     * Enterprise feature: Get battery optimization status description for logging
     */
    public static String getBatteryOptimizationStatus(final Context context) {
        final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (powerManager == null) {
            return "PowerManager unavailable";
        }

        StringBuilder status = new StringBuilder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            status.append("PowerSave: ").append(powerManager.isPowerSaveMode()).append(", ");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            status.append("Doze: ").append(powerManager.isDeviceIdleMode()).append(", ");
            status.append("BatteryOptWhitelisted: ")
                  .append(powerManager.isIgnoringBatteryOptimizations(context.getPackageName()));
        }

        return status.toString();
    }

    private PowerSaveMode() {
        // Utility class
    }
}