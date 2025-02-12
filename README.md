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
- **Image Loading:**  Coil
- **Dependency Injection:** Koin
- **Navigation:** AndroidX Navigation Component
- **UI & Animations:** Material Components, Lottie
- **Security:** AndroidX Security Crypto
- **User Authentication:** Firebase Auth, Google Play Services
- **Notifications & UI Enhancements:** SweetAlert, Snackify
- **Video Playback:** YouTube Player API

## Screenshots


<p float="left">
  <img src="https://github.com/user-attachments/assets/20f3f59b-9f19-4991-88b8-12f3b16b3f74" width="200" />
  <img src="https://github.com/user-attachments/assets/f3b5d132-eb7b-4311-a621-f0044e703c12" width="200" />
  <img src="https://github.com/user-attachments/assets/3206c6c7-7f58-42ff-9243-6e8f62d762be" width="200" />
  <img src="https://github.com/user-attachments/assets/7fa238da-cd23-4dc7-aec5-dc166feba2f3" width="200" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/a72d4a95-08a4-4106-a5bb-065a32186dbd" width="200" />
  <img src="https://github.com/user-attachments/assets/327430a3-bcf7-4e6d-9bb7-335661d3e425" width="200" />
  <img src="https://github.com/user-attachments/assets/c27913da-d9cd-4a77-b11c-5e632113696f" width="200" />
  <img src="https://github.com/user-attachments/assets/508f0df1-01ce-4904-b332-3e823e0f3a13" width="200" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/bc30095f-b825-453b-a45a-78ca68171a5f" width="200" />
  <img src="https://github.com/user-attachments/assets/2fc6a689-1ae8-4682-8981-5007a2ee4f96" width="200" />
  <img src="https://github.com/user-attachments/assets/8035fc11-5176-4bdf-a397-7a3d720c45de" width="200" />
  <img src="https://github.com/user-attachments/assets/4a1cff03-4597-4f6b-9d74-aa1df7751be6" width="200" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/b6466565-e022-4c1a-bd09-a2bc79f6a852" width="200" />
  <img src="https://github.com/user-attachments/assets/58c6659b-5060-4292-85dd-75de8405be75" width="200" />
  <img src="https://github.com/user-attachments/assets/a3a0f685-cff1-4c4c-9df1-b4de1d5e6b13" width="200" />
  <img src="https://github.com/user-attachments/assets/8ab69764-361a-4489-af86-d36c8201ed6c" width="200" />
</p>

<p float="left">
  <img src="https://github.com/user-attachments/assets/2239c4bc-58f0-4657-973c-f3393349e459" width="200" />
  <img src="https://github.com/user-attachments/assets/a862882a-619e-4348-b9e2-07a23d3aaf3e" width="200" />
  <img src="https://github.com/user-attachments/assets/a6a5427f-df9d-458a-89f9-6c2ebc1575c5" width="200" />
  <img src="https://github.com/user-attachments/assets/52a507d2-53e7-43df-ab0f-441d4b502f9b" width="200" />
</p>






