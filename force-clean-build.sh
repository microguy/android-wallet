#!/bin/bash

echo "🧹 Performing complete clean build..."
echo ""

# Step 1: Clean everything
echo "Step 1: Removing all build artifacts..."
rm -rf .gradle
rm -rf build
rm -rf wallet/build
rm -rf integration-android/build
rm -rf ~/.gradle/caches/transforms-*
rm -rf ~/.gradle/caches/build-cache-*
rm -rf ~/.gradle/caches/modules-2/files-2.1/com.github.microguy/goldcoinj
find . -name "*.apk" -type f -delete
echo "✅ Clean complete"
echo ""

# Step 2: Verify checkpoint files
echo "Step 2: Verifying checkpoint files..."
echo "=== MAINNET (checkpoints.txt) ==="
echo "First 3 lines:"
head -n 3 wallet/assets/checkpoints.txt
CHECKPOINT_COUNT=$(grep -c "^[A-Za-z0-9]" wallet/assets/checkpoints.txt)
echo "Number of checkpoints: $CHECKPOINT_COUNT"
if [ "$CHECKPOINT_COUNT" -eq 23 ]; then
    echo "✅ Mainnet checkpoint file is correct (23 checkpoints)"
else
    echo "❌ ERROR: Mainnet checkpoint file has $CHECKPOINT_COUNT checkpoints, expected 23"
    exit 1
fi
echo ""
echo "=== TESTNET (checkpoints-testnet.txt) ==="
TESTNET_COUNT=$(grep -c "^[A-Za-z0-9]" wallet/assets/checkpoints-testnet.txt)
echo "Number of testnet checkpoints: $TESTNET_COUNT"
echo ""

# Step 3: Force touch the checkpoint file to update timestamp
echo "Step 3: Updating checkpoint file timestamp..."
touch wallet/assets/checkpoints.txt
echo "✅ Timestamp updated"
echo ""

# Step 4: Build with gradle wrapper
echo "Step 4: Building APK..."
./gradlew assembleProdDebug
echo ""

# Step 5: Verify the APK
APK_PATH="wallet/build/outputs/apk/prod/debug/wallet-prod-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo "Step 5: Verifying APK contents..."
    unzip -p "$APK_PATH" assets/checkpoints.txt > /tmp/apk-checkpoints.txt
    APK_CHECKPOINT_COUNT=$(grep -c "^[A-Za-z0-9]" /tmp/apk-checkpoints.txt)
    echo "Number of checkpoints in APK: $APK_CHECKPOINT_COUNT"
    
    if [ "$APK_CHECKPOINT_COUNT" -eq 23 ]; then
        echo "✅ SUCCESS! APK contains correct checkpoints"
        echo ""
        echo "APK location: $APK_PATH"
        echo "You can now install this APK on your device"
    else
        echo "❌ ERROR: APK contains $APK_CHECKPOINT_COUNT checkpoints, expected 23"
        echo "First few lines from APK:"
        head -n 5 /tmp/apk-checkpoints.txt
    fi
else
    echo "❌ ERROR: APK not found at $APK_PATH"
fi