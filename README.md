# Goldcoin Wallet for Android

A secure Goldcoin wallet for Android devices. Based on the original Bitcoin Wallet by Andreas Schildbach, this app provides a standalone Goldcoin node implementation requiring no centralized backend.

## Features

- **No registration required** - Start using Goldcoin immediately
- **No cloud server** or web service needed - This wallet is truly peer-to-peer
- **Display of Goldcoin amount** in GLC and mGLC
- **Conversion** to and from national currencies
- **Send and receive** Goldcoin via QR codes or Goldcoin URLs
- **Address book** for regularly used Goldcoin addresses
- **System notification** for received coins
- **App widget** for Goldcoin balance
- **Automatic sync** with optimized checkpoints for fast initial setup

## Getting Started

### Download
- [Google Play Store](https://play.google.com/store/apps/details?id=com.goldcoin.wallet)
- Direct APK download (coming soon)

### Building from Source

Requirements:
- Android Studio Arctic Fox or later
- Android SDK 35
- Java 8 or higher

```bash
git clone https://github.com/microguy/android-wallet.git
cd android-wallet
```

Open the project in Android Studio and build using:
- Build → Generate Signed Bundle/APK
- Select "prodRelease" variant

### Development Setup

1. Import project into Android Studio
2. Let Gradle sync complete
3. Run on device or emulator with minimum API 23

## Technical Details

- **Minimum Android Version**: 6.0 (API level 23)
- **Target Android Version**: 14.0 (API level 35)
- **Based on**: goldcoinj (fork of bitcoinj)
- **Checkpoint System**: Optimized checkpoints updated regularly for fast sync

### Checkpoint Updates

The app uses checkpoints to speed up initial blockchain sync. Checkpoints are stored in `wallet/assets/checkpoints.txt` and follow the goldcoinj format:
- 12 bytes: chain work (big-endian)
- 4 bytes: height (big-endian)
- 80 bytes: block header

## Security

- **Private keys are only stored on your device** - Never on any server
- **Backup feature** - Encrypted wallet backup to local storage
- **PIN protection** - Optional spending PIN
- **No tracking** - We respect your privacy

### Backup Your Wallet!

Use the backup feature (Settings → Safety → Back up wallet) to create an encrypted backup of your private keys. Store this backup safely!

## Troubleshooting

### Sync Issues
- Ensure you have a stable internet connection
- The app needs to connect to the Goldcoin P2P network
- Initial sync may take 10-30 minutes depending on connection

### Balance Not Showing
- Wait for sync to complete (progress shown at top)
- Check Settings → Diagnostics → Reset blockchain

## Contributing

We welcome contributions! Please:
1. Fork the repository
2. Create a feature branch
3. Submit a pull request

For major changes, please open an issue first to discuss.

## Community

- Website: https://goldcoinproject.org
- Discord: https://discord.me/goldcoin
- Telegram: https://t.me/goldcoin

## License

This project is licensed under the GPLv3 License - see the [COPYING](COPYING) file for details.

## Credits

- Original Bitcoin Wallet by Andreas Schildbach
- goldcoinj development team
- All contributors to this project

## Disclaimer

This software is provided "as is" without warranty of any kind. Users are responsible for securing their own wallets and private keys. Always keep backups!

---

**Remember**: Not your keys, not your coins. This wallet gives you full control and full responsibility.
