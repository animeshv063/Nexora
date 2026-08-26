<div align="center">

# 🛍️ Nexora — Premium Android E-Commerce Experience

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=android)](https://developer.android.com/jetpack/compose)
[![Dagger Hilt](https://img.shields.io/badge/Dagger%20Hilt-KSP-00C853?style=for-the-badge&logo=dagger)](https://dagger.dev/hilt/)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth-FFCA28?style=for-the-badge&logo=firebase)](https://firebase.google.com)
[![Razorpay](https://img.shields.io/badge/Razorpay-Payment%20Gateway-0C2340?style=for-the-badge&logo=razorpay)](https://razorpay.com)

A high-performance, full-featured modern e-commerce mobile application built for Android utilizing **Jetpack Compose (Material 3)**, **Clean Architecture (MVVM + Use Cases)**, **Dagger Hilt Dependency Injection**, and **Firebase Cloud Firestore**.

</div>

---

## 🌟 Key Highlights & Capabilities

### 🎨 Modern Dark-Mode UI & Smooth Micro-Interactions
- Custom high-contrast Dark Palette with fluid Compose transitions and elevation shadows.
- Dynamic carousel banners, category carousels, and grid product listings.
- Real-time live search with instant multi-field product filtering (Name, Category, Description).

### 🛒 Complete End-to-End E-Commerce Flow
- **Product Details**: Comprehensive view with stock counter, real-time discount calculations, dynamic user ratings, and instant wishlist/cart toggling.
- **Cart & Wishlist**: Real-time quantity adjustments, price calculations, and synchronized cloud state.
- **Intelligent Checkout**:
  - One-tap **GPS Location Auto-Detection** via Google Play Services Geocoder to auto-fill delivery addresses.
  - Test-ready **Razorpay Payment Gateway** integration with Cash on Delivery (COD) support.
- **Order Tracking & History**: Real-time status tracking (`Pending`, `Confirmed`, `Shipped`, `Delivered`, `Cancelled`) with in-app order cancellation.

### 👑 Real-Time Owner / Admin Console
- Built-in secure admin authentication for store managers.
- Real-time Firestore management:
  - **Products**: Add, edit, delete, and manage inventory with live image previews and sample generators.
  - **Categories**: Create and manage store categories with live icons.
  - **Promotional Banners**: Add and manage home screen marketing campaigns.
  - **Inventory Safeguards**: Auto-restock mechanism and out-of-stock validation.

---

## 🏗️ Architecture & Engineering Design

Built adhering to strict **SOLID principles** and Google's recommended **Clean Architecture** patterns:

```
app/
├── data/
│   ├── models/            # Remote entity models & DTOs
│   └── repo/              # Concrete Repository Implementation (Firestore / Auth)
├── domain/
│   ├── di/                # Hilt Dependency Injection Modules
│   ├── models/            # Pure Kotlin Domain Business Models
│   ├── repo/              # Repository Interfaces (Abstraction Layer)
│   └── useCase/           # Granular Single-Responsibility Use Cases (AddProduct, PlaceOrder, etc.)
├── presentation/
│   ├── navigation/        # Type-Safe Navigation & Bottom Bar Routing
│   ├── screens/           # Modular Compose Screens (Home, Details, Cart, Checkout, Admin, Profile)
│   ├── utils/             # Image Loaders (Coil), Location Helpers, Razorpay Handlers
│   └── viewModels/        # Reactive StateFlow ViewModels
└── ui/theme/              # Design System Tokens, Colors, Typography & Shapes
```

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **UI & Layout** | Jetpack Compose (Material 3), Navigation Compose |
| **Language** | Kotlin (Coroutines, StateFlow, Flow) |
| **Architecture** | Clean Architecture + MVVM + Repository Pattern + Use Cases |
| **Dependency Injection** | Dagger Hilt with Kotlin Symbol Processing (KSP) |
| **Database & Auth** | Firebase Cloud Firestore, Firebase Authentication |
| **Image Loading** | Coil 2.x (OkHttp Interceptor, Memory & Disk Caching) |
| **Location Services** | Google Play Services Location API & Geocoder |
| **Payment Gateway** | Razorpay Android SDK |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Ladybug (2024.2+)** or latest
- **JDK 17 or JDK 21**
- **Android SDK API 26+** (Android 8.0 Oreo or higher)

### Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/animeshv063/ShoppingApp.git
   ```
2. **Add Firebase Configuration**:
   - Create a project on [Firebase Console](https://console.firebase.google.com).
   - Enable **Firebase Authentication** (Email/Password) and **Cloud Firestore**.
   - Download `google-services.json` and place it in the `app/` directory (see `app/google-services.json.example`).
3. **Build & Run**:
   - Open the project in Android Studio.
   - Run `./gradlew compileDebugKotlin` to compile.
   - Run the application on your physical device or emulator.

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).
