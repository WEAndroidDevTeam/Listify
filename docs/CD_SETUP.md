# Listify CD Pipeline Setup Guide

This document explains how to configure the CD pipeline for automated deployment to Firebase App Distribution.

## Required GitHub Secrets

Go to **GitHub → WEAndroidDevTeam/Listify → Settings → Secrets and variables → Actions → New repository secret**

Add all of the following:

### 1. Android Keystore (APK Signing)

```bash
# Generate a keystore if you don't have one
keytool -genkey -v \
  -keystore listify.jks \
  -alias listify \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000

# Encode it to base64
base64 -i listify.jks | pbcopy   # macOS
base64 listify.jks               # Linux
```

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | Base64-encoded contents of listify.jks |
| `KEY_ALIAS` | `listify` (or whatever alias you used) |
| `KEY_PASSWORD` | Key password you set during keytool |
| `STORE_PASSWORD` | Store password you set during keytool |

### 2. Firebase App Distribution

1. Go to [Firebase Console](https://console.firebase.google.com) → Create project `Listify`
2. Add Android app with package `com.listify`
3. Get the **App ID** from Project Settings → Your apps

```
Format: 1:xxxxxxxxxxxx:android:xxxxxxxxxxxxxxxx
```

4. Create a Service Account:
   - Firebase Console → Project Settings → Service Accounts
   - Click **Generate new private key** → download JSON file
   - Copy the entire JSON content

| Secret Name | Value |
|---|---|
| `FIREBASE_APP_ID` | App ID from Firebase (e.g. `1:123456:android:abcdef`) |
| `FIREBASE_SERVICE_ACCOUNT` | Full JSON content of the service account key |

5. Add testers group in Firebase:
   - Firebase Console → App Distribution → Testers & Groups
   - Create group named `internal-testers`
   - Add tester emails

### 3. Slack Notifications

1. Go to [api.slack.com/apps](https://api.slack.com/apps) → Create New App → From scratch
2. Add **Incoming Webhooks** feature → Activate
3. Click **Add New Webhook to Workspace** → Select `#android-releases` channel
4. Copy the webhook URL

| Secret Name | Value |
|---|---|
| `SLACK_WEBHOOK_URL` | `https://hooks.slack.com/services/T.../B.../...` |

## How the Pipeline Works

```
Push to main
     │
     ▼
Run unit tests (18 tests)
     │
     ▼
Build release APK
     │
     ▼
Sign APK with keystore
     │
     ▼
Deploy to Firebase App Distribution → internal-testers
     │
     ▼
Create GitHub Release + tag
     │
     ▼
Notify #android-releases Slack channel
```

## Triggering a Manual Deploy

You can trigger a deploy manually from GitHub Actions with custom release notes:

1. GitHub → Actions → Listify CD → Run workflow
2. Enter release notes
3. Click **Run workflow**

## Checking Deploy Status

- **GitHub Actions**: github.com/WEAndroidDevTeam/Listify/actions
- **Firebase**: console.firebase.google.com → Listify → App Distribution
- **Slack**: #android-releases channel
