# Lab 10 – Firebase Authentication & Glide

**Student:** Kieu Bao Giang – 2474802010095

Package: `vn.edu.vlu.lab10`

## Features
- **Firebase Email/Password authentication** (`AuthActivity`)
  - Login and Register tabs (`MaterialButtonToggleGroup`), plus a progress indicator while Firebase works.
  - Client-side checks: email format (`Patterns.EMAIL_ADDRESS`), password of at least 6 characters, matching confirm password.
  - Firebase errors handled: weak password, invalid email, wrong credentials, email already registered, account disabled, no network.
  - Auto-login: when `auth.currentUser != null` the app opens the gallery directly.
- **Photo gallery with Glide** (`MainActivity`, `PhotoAdapter`)
  - 10 HTTPS photos from Picsum shown in a RecyclerView + CardView (12dp corners, 4dp elevation).
  - Glide uses a placeholder, an error image, a cross-fade and `DiskCacheStrategy.ALL`.
  - `onViewRecycled()` clears pending requests.
- **Logout** in the toolbar: `auth.signOut()`, then `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`, so Back cannot return to the gallery.
- **Bonus 1:** "Forgot password?" calls `sendPasswordResetEmail()`.
- **Bonus 2:** circular avatar with Glide `circleCrop()`, seeded by the user's email.
- 100% View Binding, no `findViewById()`.

## Setup
1. Put your own `google-services.json` in `app/`. It is in `.gitignore` and must not be pushed to a public repository.
2. In the Firebase Console, enable Email/Password under **Authentication → Sign-in method**.
3. Register the debug SHA-1 (`gradlew signingReport`) for package `vn.edu.vlu.lab10`.

## Notes on versions
The project uses the current toolchain instead of the versions in the handout:
- Android Gradle Plugin 9.4.1 with built-in Kotlin, so there is no `kotlin.android` plugin or `kotlinOptions`.
- KSP 2.3.12.
- compileSdk / targetSdk 37.
- Firebase BoM 34.9.0. BoM 34 removed the `-ktx` artifacts, so the dependency is `firebase-auth`; its Kotlin extensions are now included in it.

Target SDK 35+ draws behind the system bars, so both screens use `fitsSystemWindows`.

## AI use
AI helped reorganize and present the content in a logical, easy-to-understand order, breaking it down into specific steps to make completing the assignment easier.
