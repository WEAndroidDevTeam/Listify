# Listify Post-Production Monitoring & Auto Bug-Fix Setup

## Architecture

```
Production App
     │ crash detected
     ▼
Firebase Crashlytics
     │ webhook alert
     ▼
GitHub: repository_dispatch (crashlytics_webhook.yml)
     │
     ▼
GitHub: auto_bugfix.yml triggers
     │
     ├── Creates Jira Bug ticket (LIS-xxx) with stack trace
     ├── Calls Claude AI API to analyze crash + generate fix
     ├── Applies fix to new branch: autofix/lis-xxx-crash-fix
     ├── Opens PR with fix for human review
     ├── Updates Jira ticket with PR link
     └── Notifies #android-releases Slack channel
```

## Required Secrets

Add to GitHub → Settings → Secrets:

| Secret | How to get it |
|---|---|
| `ANTHROPIC_API_KEY` | console.anthropic.com → API keys |
| `JIRA_API_TOKEN_B64` | base64 encode of `email:api_token` — get token at id.atlassian.com/manage-profile/security/api-tokens |
| `SLACK_WEBHOOK_URL` | api.slack.com → Your apps → Incoming Webhooks |

## Setting Up Firebase Crashlytics Webhook

1. Firebase Console → Your Listify project
2. Go to **Crashlytics** → **Alerts** (bell icon)  
3. Click **Add webhook**
4. Webhook URL:
   ```
   https://api.github.com/repos/WEAndroidDevTeam/Listify/dispatches
   ```
5. Headers:
   ```
   Authorization: token YOUR_GITHUB_TOKEN
   Content-Type: application/json
   Accept: application/vnd.github+json
   ```
6. Select alert types: **New fatal issue**, **Regression**, **Velocity alert**

## Testing the Auto Bug-Fix Loop Manually

Go to **GitHub → Actions → Listify Auto Bug-Fix Loop → Run workflow**

Enter:
- Crash title: `NullPointerException in ProductListFragment`  
- Stack trace: paste a real stack trace
- Affected version: `1.0.0`

The pipeline will:
1. Create a Jira bug ticket
2. Ask Claude to analyze and fix it
3. Open a PR with the generated fix
4. Notify Slack

## Review Process

Auto-fix PRs are **never merged automatically** — they always require human review.
The confidence level in the PR description guides urgency:
- `high` — likely correct, review and merge quickly
- `medium` — review logic carefully before merging  
- `low` — treat as analysis only, implement fix manually

## Jira Token Setup

```bash
# Generate base64 encoded Jira credentials
echo -n "your.email@domain.com:YOUR_JIRA_API_TOKEN" | base64
# Paste the output as JIRA_API_TOKEN_B64 secret
```
