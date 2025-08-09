# Goldcoin Android Wallet Release Testing Checklist

## Critical Changes in This Release
- ✅ Updated to API level 35 (Android 15) - Required by Google Play Store
- ✅ Updated goldcoinj dependency to latest version
- ✅ New checkpoint format with only 2 months of data (23 checkpoints)
- ✅ Removed deprecated Android Beam NFC functionality
- ✅ Fixed MultiDex issues

## Pre-Release Testing Required

### 1. Fresh Install Testing
- [ ] Uninstall any existing app version
- [ ] Install the new APK
- [ ] Verify app starts without crashing
- [ ] Verify blockchain sync starts from block ~2,288,160 (not genesis)
- [ ] Verify sync completes in reasonable time (< 10 minutes on good connection)
- [ ] Create new wallet and verify it works

### 2. Upgrade Testing (CRITICAL - Most users will upgrade)
- [ ] Install previous Play Store version first
- [ ] Create a wallet with some test transactions
- [ ] Upgrade to new APK without uninstalling
- [ ] Verify wallet balance is preserved
- [ ] Verify transaction history is preserved
- [ ] Verify app doesn't crash or lose data
- [ ] Verify blockchain continues syncing properly

### 3. Core Functionality Testing
- [ ] Send transaction (testnet first, then mainnet with small amount)
- [ ] Receive transaction
- [ ] Generate new receive address
- [ ] Scan QR code
- [ ] Display QR code
- [ ] Backup wallet (export)
- [ ] Restore wallet from backup
- [ ] Check exchange rate display
- [ ] Verify all settings work

### 4. Network/Sync Testing
- [ ] Test on WiFi
- [ ] Test on mobile data
- [ ] Test with poor connection
- [ ] Verify app handles network disconnection gracefully
- [ ] Verify checkpoint-based sync works (should start from block ~2,288,160)

### 5. Device Compatibility Testing
- [ ] Test on Android 5.0 (API 21 - minimum supported)
- [ ] Test on Android 10
- [ ] Test on Android 13
- [ ] Test on Android 14
- [ ] Test on Android 15 (API 35 - latest)
- [ ] Test on different screen sizes

### 6. Edge Cases
- [ ] Test with wallet that has many transactions (>100)
- [ ] Test with very old wallet file
- [ ] Test app behavior when storage is almost full
- [ ] Test currency conversion with different locales

## Build Verification
- [ ] APK is signed with correct production key
- [ ] Version code is incremented (current: 64109)
- [ ] Version name is updated appropriately
- [ ] Package name is correct: `de.schildbach.wallet.goldcoin`

## Final Checks
- [ ] No debug logging enabled
- [ ] No test configuration left in code
- [ ] All translations working
- [ ] Privacy policy and terms updated if needed
- [ ] Release notes prepared for Play Store

## Rollback Plan
If issues are discovered after release:
1. Keep previous APK version available
2. Monitor crash reports closely for first 48 hours
3. Have hotfix process ready
4. Communication plan for users if needed

## Sign-off
- [ ] Developer testing complete
- [ ] Community beta testing complete (if applicable)
- [ ] Ready for production release

---
Remember: With hundreds of active users, stability is more important than new features.