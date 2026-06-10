#!/bin/bash
# setup_secrets.sh — Set all GitHub Actions secrets for Listify CD pipeline
# Usage: GITHUB_TOKEN=ghp_xxx bash setup_secrets.sh
#
# Prerequisites: gh CLI installed (brew install gh) or set TOKEN manually

REPO="WEAndroidDevTeam/Listify"
TOKEN="${GITHUB_TOKEN:-}"

if [ -z "$TOKEN" ]; then
  echo "Error: Set GITHUB_TOKEN environment variable first"
  exit 1
fi

set_secret() {
  local name=$1
  local value=$2
  curl -s -X PUT \
    -H "Authorization: token $TOKEN" \
    -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/$REPO/actions/secrets/$name" \
    -d "{\"encrypted_value\":\"$(echo -n "$value" | base64)\"}" \
    > /dev/null
  echo "✅ Set $name"
}

echo "Setting up GitHub secrets for $REPO..."
echo ""

# Prompt for each secret
read -p "Path to listify.jks keystore: " KEYSTORE_PATH
KEYSTORE_B64=$(base64 -i "$KEYSTORE_PATH")
set_secret "KEYSTORE_BASE64" "$KEYSTORE_B64"

read -p "Key alias: " KEY_ALIAS
set_secret "KEY_ALIAS" "$KEY_ALIAS"

read -s -p "Key password: " KEY_PASSWORD; echo
set_secret "KEY_PASSWORD" "$KEY_PASSWORD"

read -s -p "Store password: " STORE_PASSWORD; echo
set_secret "STORE_PASSWORD" "$STORE_PASSWORD"

read -p "Firebase App ID (1:xxx:android:xxx): " FIREBASE_APP_ID
set_secret "FIREBASE_APP_ID" "$FIREBASE_APP_ID"

read -p "Path to Firebase service account JSON: " SA_PATH
SA_CONTENT=$(cat "$SA_PATH")
set_secret "FIREBASE_SERVICE_ACCOUNT" "$SA_CONTENT"

read -p "Slack webhook URL: " SLACK_URL
set_secret "SLACK_WEBHOOK_URL" "$SLACK_URL"

echo ""
echo "🎉 All secrets configured! Trigger a deploy by pushing to main."
