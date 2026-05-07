# exercise1
KOTLIN JETPACK COMPOSE MOBILE SHOP

# MobileShop 🛍️

A Nike-inspired Android shopping app built with **Kotlin** and **Jetpack Compose**. Users can browse products, view product details, select sizes, add items to a bag, and see a running total — all without a database, using an in-memory list.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material 3) |
| Image Loading | Coil (`coil-compose:2.6.0`) |
| State Management | `remember` + `mutableStateMapOf` |
| Navigation | Manual screen state (no NavController) |
| Build System | Gradle with Kotlin DSL (`.kts`) |
| Min SDK | 23 (Android 6.0) |
| Target SDK | 36 |
 
---

## Prerequisites

Before you begin, make sure you have the following installed:

- **Android Studio** (Hedgehog or later) or **IntelliJ IDEA** with the Android plugin
- **JDK 11** or higher
- **Android SDK** with API level 36 installed
- A configured **Android Virtual Device (AVD)** — Pixel series recommended
---
### Key files explained
**`MainActivity.kt`**
The entire application is contained in this single file. It includes:
- `MainActivity` — the Android entry point, calls `setContent { ShopApp() }`
- `Product` — data class representing one product
- `allProducts` — the in-memory list acting as the product database
- `categories` — list of filter chip labels
- `ShopApp()` — root composable managing which screen is shown
- `HomeScreen()` — product grid with hero banner and category filters
- `ProductCard()` — individual card in the grid
- `DetailScreen()` — full product detail with size selector
- `CartScreen()` — shopping bag with totals
- `CartItem()` — individual row in the cart
- Colour constants: `OrangeRed`, `DarkBg`, `CardBg`, `LightGray`, `MutedText`
  **`AndroidManifest.xml`**
  Declares `MainActivity` as the launcher activity and grants internet permission for loading network images.

**`app/build.gradle.kts`**
Configures the Compose build feature, sets SDK versions, and declares all dependencies including Coil for image loading.

**`res/values/themes.xml`**
Sets the app theme to `android:Theme.Material.Light.NoActionBar` so Compose can own the entire screen.
 
---

## Configuration

### 1. Clone or open the project
### 2. Sync Gradle
### 3. Verify `build.gradle.kts`
### 4. Build and Run


