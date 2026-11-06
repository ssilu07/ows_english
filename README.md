# OWS English (One Word Substitution with Tricks)

A modern Android application designed to help students master English Vocabulary and One Word Substitutions through mnemonics, visual memory tricks, and real-time updates.

## 🚀 Features
- **Tricky Vocabulary & Mnemonics**: Visual cues and associative tricks to remember complex vocabulary easily.
- **Rich Media Cards**: Illustrated vocabulary cards with meanings, keys, and descriptions.
- **Real-Time Database**: Powered by Firebase Realtime Database for instant content sync.
- **Monetization Ready**: Integrated with Google AdMob (Adaptive Banners and Smart Interstitial Ads).
- **Modern Android Architecture**: Fully updated to Android 16 (API Level 36), Java 17, and AndroidX.
- **Social Sharing**: One-tap share of vocabulary tricks to WhatsApp, Telegram, etc.

## 🛠️ Tech Stack & Libraries
- **Language**: Java / Android SDK (minSdk 23, targetSdk 36, compileSdk 36)
- **Architecture**: Single-Activity, Fragments & Navigation Component
- **Network & Database**: Firebase Realtime Database & Firebase Auth
- **Image Loading**: Glide with Photoview pinch-to-zoom support
- **Animations**: Airbnb Lottie
- **Monetization**: Google Mobile Ads SDK (`play-services-ads:23.6.0`)
- **CI/CD**: GitHub Actions for automated building and Google Play publishing

## 📦 Build & Run
```bash
# Clone the repository
git clone https://github.com/ssilu07/ows_english.git

# Build debug APK
./gradlew assembleDebug

# Build release App Bundle (.aab)
./gradlew bundleRelease
```

## 📄 License
All rights reserved.
