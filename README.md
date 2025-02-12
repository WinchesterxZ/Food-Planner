## Meal Planner App

### Overview
The Meal Planner App helps users plan their weekly meals, explore meal categories, and save their favorite meals. The app integrates Firebase for authentication and backup, Room for local storage, and Retrofit for API calls.

### Features
- **Meal of the Day:** Users can view a random meal for inspiration.
- **Meal Search:** Search meals by country, ingredient, or category.
- **Category & Country Lists:** Browse available categories and popular meals by country.
- **Favorites Management:** Users can add or remove meals from favorites (Stored in Room).
- **Backup & Sync:** Users can back up their data to Firebase and restore it on login.
- **Meal Planning:** Plan meals for the current week.
- **Offline Access:** View saved meals and plans without an internet connection.
- **Authentication:**
  - Login via Google, Facebook, or Twitter (Firebase Authentication).
  - Data persistence using SharedPreferences and Firebase.
  - Guest users can only browse categories and view the meal of the day.
- **Meal Details:**
  - Name, Image, Country of Origin
  - Ingredients (Displayed as images if available)
  - Preparation Steps
  - Embedded video (Not just a URL)
  - Favorite toggle button
- **Animations:** The app features a Lottie-powered splash screen.

### Tech Stack
- **Programming Language:** Kotlin
- **Architecture:** MVVM
- **Local Storage:** Room Database
- **Remote API:** TheMealDB API (Retrofit + Gson)
- **Image Loading:** Glide, Coil
- **Dependency Injection:** Koin
- **Navigation:** AndroidX Navigation Component
- **UI & Animations:** Material Components, Lottie
- **Security:** AndroidX Security Crypto
- **User Authentication:** Firebase Auth, Google Play Services
- **Notifications & UI Enhancements:** SweetAlert, Snackify
- **Video Playback:** YouTube Player API


