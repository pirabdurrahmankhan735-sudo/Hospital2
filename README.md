# Hospital Manager (Phase 1)
Open this folder in Android Studio (Koala or newer, JDK 17). Let Gradle sync (internet needed once).
Android Studio creates the Gradle wrapper automatically; Android SDK 34 is installed from SDK Manager.
Debug APK: Build > Build APK(s). Release AAB: Build > Generate Signed Bundle / APK > Android App Bundle (create your own keystore).
Included: Room database, patients, doctors (verified/unverified), appointments, dashboard, dark mode, English/Urdu/Arabic with RTL.
Not yet included: login/roles, pharmacy, prescriptions PDF, lab, accounting, blood bank (later phases).
No real hospital/doctor data is bundled; enter your own verified data.
NOTE: this code was written without being compiled or tested. Please report any build error.

## APK/AAB without Android Studio (free, in the cloud)
1. Create a free GitHub account and a new repository; upload all files of this folder (including the .github folder).
2. Open the Actions tab > "Build APK and AAB" > wait for the green tick.
3. Download "outputs": it contains the debug APK (installable on any Android phone) and a release AAB.
4. The AAB must be signed with your own keystore before Play Store upload (Play App Signing in Play Console handles this).
