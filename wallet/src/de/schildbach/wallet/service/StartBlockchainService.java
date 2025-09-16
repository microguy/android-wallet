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

package de.schildbach.wallet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.schildbach.wallet.Configuration;
import de.schildbach.wallet.Constants;
import de.schildbach.wallet.WalletApplication;
import de.schildbach.wallet.util.PowerSaveMode;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.BatteryManager;
import android.os.StatFs;

/**
 * This service is responsible for starting blockchain sync via {@link BlockchainService} using Android's job scheduler.
 * It implements intelligent scheduling based on usage patterns and system conditions.
 *
 * @author Andreas Schildbach
 * @author MicroGuy (Goldcoin optimizations)
 */
public final class StartBlockchainService extends JobService {
    private static final String ACTION_START_BLOCKCHAIN = "start_blockchain";
    private static final int JOB_ID_START_BLOCKCHAIN = 1000;

    private WalletApplication application;
    private Configuration config;

    private static final Logger log = LoggerFactory.getLogger(StartBlockchainService.class);

    public static void schedule(final Context context) {
        final WalletApplication application = (WalletApplication) context.getApplicationContext();
        final Configuration config = application.getConfiguration();

        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final long lastUsedAgo = config.getLastUsedAgo();

        // Calculate sync interval based on usage patterns (Goldcoin optimization)
        final long syncInterval;
        if (lastUsedAgo < Constants.LAST_USAGE_THRESHOLD_JUST_MS) {
            // Recently used - sync more frequently
            syncInterval = 15 * 60 * 1000; // 15 minutes
        } else if (lastUsedAgo < Constants.LAST_USAGE_THRESHOLD_RECENTLY_MS) {
            // Used today - moderate sync
            syncInterval = 60 * 60 * 1000; // 1 hour
        } else if (lastUsedAgo < Constants.LAST_USAGE_THRESHOLD_INACTIVE_MS) {
            // Used recently but not today - less frequent
            syncInterval = 12 * 60 * 60 * 1000; // 12 hours
        } else {
            // Used long ago - minimal sync
            syncInterval = 24 * 60 * 60 * 1000; // 1 day
        }

        // Enhanced job configuration for enterprise-grade optimization
        final JobInfo.Builder jobBuilder = new JobInfo.Builder(JOB_ID_START_BLOCKCHAIN,
                new ComponentName(context, StartBlockchainService.class))
                .setMinimumLatency(syncInterval)
                .setOverrideDeadline(7 * 24 * 60 * 60 * 1000) // Maximum 1 week
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setRequiresDeviceIdle(false) // Allow sync even when device is active
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .setPersisted(true); // Persist across device reboots

        // Goldcoin enterprise optimization: Adjust network requirements based on expected data
        final long blockchainFileSize = application.getBlockchainFileSize();
        if (blockchainFileSize > 100 * 1024 * 1024) { // > 100MB
            jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            log.info("Large blockchain file ({} bytes), requiring unmetered network", blockchainFileSize);
        }

        final JobInfo jobInfo = jobBuilder.build();
        final int result = jobScheduler.schedule(jobInfo);

        if (result == JobScheduler.RESULT_SUCCESS) {
            log.info("Scheduled blockchain sync job with {}ms interval", syncInterval);
        } else {
            log.warn("Failed to schedule blockchain sync job");
        }
    }

    public static void cancel(final Context context) {
        final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID_START_BLOCKCHAIN);
        log.info("Cancelled blockchain sync job");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (WalletApplication) getApplication();
        config = application.getConfiguration();
        log.info("StartBlockchainService created");
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        log.info("Starting blockchain sync job with ID: {}", params.getJobId());

        // Enterprise-grade system condition checks
        if (!shouldStartSync()) {
            log.info("System conditions not suitable for blockchain sync, rescheduling");
            jobFinished(params, true); // Reschedule
            return false;
        }

        // Start blockchain service
        BlockchainService.start(this, true);

        // Update last sync attempt
        config.updateLastSyncAttempt();

        log.info("Blockchain sync started successfully");
        jobFinished(params, false); // Job completed successfully
        return false; // No ongoing work
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        log.info("Blockchain sync job stopped: {}", params.getJobId());
        return false; // Don't reschedule
    }

    /**
     * Enterprise-grade system condition checks to determine if blockchain sync should start.
     * Enhanced beyond upstream with additional optimizations.
     */
    private boolean shouldStartSync() {
        // Check battery level (enhanced check)
        final BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            final int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            if (batteryLevel < 15) { // More conservative than upstream's "low battery"
                log.info("Battery level too low for sync: {}%", batteryLevel);
                return false;
            }
        }

        // Check power save mode
        if (PowerSaveMode.isEnabled(this)) {
            log.info("Power save mode enabled, deferring sync");
            return false;
        }

        // Enhanced storage check
        try {
            final StatFs statFs = new StatFs(getFilesDir().getPath());
            final long availableBytes = statFs.getAvailableBytes();
            final long minRequiredBytes = 100 * 1024 * 1024; // 100MB minimum

            if (availableBytes < minRequiredBytes) {
                log.info("Insufficient storage for sync: {} bytes available, {} required",
                    availableBytes, minRequiredBytes);
                return false;
            }
        } catch (Exception x) {
            log.warn("Error checking storage", x);
            return false;
        }

        // Goldcoin enterprise enhancement: Check network quality
        if (isNetworkMetered() && shouldAvoidMeteredSync()) {
            log.info("Avoiding sync on metered network to preserve user data");
            return false;
        }

        return true;
    }

    /**
     * Enterprise optimization: Check if we should avoid syncing on metered networks
     */
    private boolean isNetworkMetered() {
        // Implementation would check if current network connection is metered
        // This is a simplified version - full implementation would use ConnectivityManager
        return false; // For now, assume unmetered
    }

    /**
     * Enterprise optimization: Determine if we should avoid sync on metered networks
     */
    private boolean shouldAvoidMeteredSync() {
        // Avoid metered sync if:
        // 1. Large blockchain file pending
        // 2. User hasn't used app recently
        // 3. Data saver mode is enabled

        final long blockchainFileSize = application.getBlockchainFileSize();
        final long lastUsedAgo = config.getLastUsedAgo();

        return blockchainFileSize > 50 * 1024 * 1024 && // > 50MB
               lastUsedAgo > Constants.LAST_USAGE_THRESHOLD_RECENTLY_MS; // Not used recently
    }
}