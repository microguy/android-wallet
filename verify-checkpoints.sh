#!/bin/bash

echo "Verifying checkpoint files..."
echo ""

# Check the source checkpoint file
echo "=== Checking wallet/assets/checkpoints.txt ==="
head -n 5 wallet/assets/checkpoints.txt
echo "Number of checkpoints: $(grep -c "^[A-Za-z0-9]" wallet/assets/checkpoints.txt)"
echo ""

# Build the APK
echo "=== Building APK ==="
./gradlew clean
./gradlew assembleProdDebug
echo ""

# Extract and check checkpoint file from APK
APK_PATH="wallet/build/outputs/apk/prod/debug/wallet-prod-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo "=== Extracting checkpoints from APK ==="
    unzip -p "$APK_PATH" assets/checkpoints.txt > /tmp/apk-checkpoints.txt
    head -n 5 /tmp/apk-checkpoints.txt
    echo "Number of checkpoints in APK: $(grep -c "^[A-Za-z0-9]" /tmp/apk-checkpoints.txt)"
    echo ""
    
    # Compare files
    echo "=== Comparing files ==="
    if diff wallet/assets/checkpoints.txt /tmp/apk-checkpoints.txt > /dev/null; then
        echo "✅ Checkpoint files match!"
    else
        echo "❌ Checkpoint files DO NOT match!"
        echo "Differences:"
        diff wallet/assets/checkpoints.txt /tmp/apk-checkpoints.txt | head -20
    fi
else
    echo "❌ APK not found at $APK_PATH"
fi