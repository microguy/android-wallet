/*
 * Copyright 2011-2015 the original author or authors.
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

// import de.schildbach.wallet.Configuration;  // Not needed after removing InactivityNotificationService
// import de.schildbach.wallet.Constants;  // Not needed after removing InactivityNotificationService
import de.schildbach.wallet.WalletApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Andreas Schildbach
 */
public class BootstrapReceiver extends BroadcastReceiver {
    private static final Logger log = LoggerFactory.getLogger(BootstrapReceiver.class);

    @Override
    public void onReceive(final Context context, final Intent intent) {
        log.info("got broadcast: " + intent);

        final WalletApplication application = (WalletApplication) context.getApplicationContext();

        final boolean bootCompleted = Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction());
        final boolean packageReplaced = Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction());

        if (packageReplaced || bootCompleted) {
            // Removed: UpgradeWalletService - not needed for Goldcoin
            // if (packageReplaced)
            //     UpgradeWalletService.startUpgrade(context);

            // make sure there is always an alarm scheduled
            BlockchainService.scheduleStart(application);

            // Removed: InactivityNotificationService - not needed for Goldcoin
            // final Configuration config = application.getConfiguration();
            // if (config.remindBalance() && config.hasBeenUsed()
            //         && config.getLastUsedAgo() > Constants.LAST_USAGE_THRESHOLD_INACTIVE_MS)
            //     InactivityNotificationService.startMaybeShowNotification(context);
        }
    }
}
