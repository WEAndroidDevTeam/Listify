# Listify UI Testing with Maestro

## What is Maestro?

Maestro is a mobile UI testing framework that uses simple YAML flows to describe
user interactions (tap, swipe, assert). It catches runtime crashes that unit tests miss —
exactly the kind of bug (IndexOutOfBoundsException in the grid) we hit during development.

## Flows

| Flow | What it verifies |
|---|---|
| `01_product_grid.yaml` | App launches, product grid + search bar render |
| `02_product_detail.yaml` | Tapping a product opens detail screen with Add to Cart |
| `03_add_to_cart.yaml` | Quantity stepper + Add to Cart shows confirmation |
| `04_search.yaml` | Search bar filters the product list |

## Running Locally

```bash
# Install Maestro (one time)
curl -Ls "https://get.maestro.mobile.dev" | bash

# Run all flows (builds, installs, tests)
./.maestro/run_local.sh
```

## In CI

`maestro.yml` runs on every PR to main/develop:
1. Builds debug APK
2. Spins up an Android emulator (API 30, Pixel 5)
3. Installs the app and runs all 4 flows
4. Uploads screenshots + JUnit report
5. Comments pass/fail on the PR

## Adding a New Flow

Create `.maestro/NN_description.yaml`:
```yaml
appId: com.listify
---
- launchApp
- assertVisible: "Some text"
- tapOn:
    id: "someViewId"
```

Element IDs come from the `android:id` in the layout XML (without `@+id/`).
