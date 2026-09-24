
# NutriFind Overview, Setup, and Retrospective

[Video Walkthrough](https://youtu.be/oGpYUiHByno)

[Github Repo](https://github.com/MaldonadoJack/cst438-Project1)

## Overview

NutriFind is an Android calorie tracker. A user creates an account on the device, searches foods through the [FatSecret Platform API](https://platform.fatsecret.com/), views nutrition facts, and saves one serving to that user's log for today.

The Android package is `com.example.donorproject`. The launcher name is NutriFind.

## Introduction

* How was communication managed
  * We had communicated primarily on Google Messages and in person after Operating Systems in the Library
* How many stories/issues were initially considered
  * 26 GitHub issues were opened.
* How many stories/issues were completed
  * 24 issues are closed and 2 are still open (#5 favorites, #14 past food logs).

## Team Retrospective

### Julian Mendoza

1. Julian's pull requests are [here](https://github.com/MaldonadoJack/cst438-Project1/pulls?q=is%3Apr+author%3Ajulian-men)
1. Julian's Github issues are [here](https://github.com/MaldonadoJack/cst438-Project1/issues?q=is%3Aissue+author%3Ajulian-men)

#### What was your role / which stories did you work on

Julian built local accounts, Room, password hashing, login checks, logout, the Compose home screen, and the user-linked food log. Issues include #6, #7, #8, #9, #24, #39, #44, and #50.

+ What was the biggest challenge?
  + Connecting separately built screens through one user id and a shared database.
+ Why was it a challenge?
  + Login, search, and nutrition facts were merged before each food entry had an owner.
+ How was the challenge addressed?
  + Pull request #45 passed the logged-in user's database id into Home and added the `food_log` table. Pull request #47 then built today's log on that table.
+ Favorite / most interesting part of this project
  + Favorite part of this project was connecting the screens and letting the project actually flow.
+ If you could do it over, what would you change?
  + I would totally change the API.
+ What is the most valuable thing you learned?
  + How to manage my issues and maintaining communication with my team when things change.

### Jack Maldonado

1. Jack's pull requests are [here](https://github.com/MaldonadoJack/cst438-Project1/pulls?q=is%3Apr+author%3AMaldonadoJack)
1. Jack's Github issues are [here](https://github.com/MaldonadoJack/cst438-Project1/issues?q=is%3Aissue+author%3AMaldonadoJack)

#### What was your role / which stories did you work on

Jack set up the Android project, the home screen, FatSecret name search, the search results, and today's food log. Issues include #2, #3, #15, and #33. Pull request #47 also closed #10, #11, and #16.

+ What was the biggest challenge?
  + FatSecret food search.
+ Why was it a challenge?
  + The API only responds from allowlisted IP addresses.
+ How was the challenge addressed?
  + The README setup notes documented the token and the IP allowlist. Allocated API work to those with a stable IP.
+ Favorite / most interesting part of this project
  + My favorite part was setting up the API and getting it to work with the search function.
+ If you could do it over, what would you change?
  + I think I would change the API we used.
+ What is the most valuable thing you learned?
  + The most valuable thing I learned was how to connect a REST API to an Android Studio project

### Daniel

GitHub and git author: `dabarker32`. Feature branches are named `feature/Daniel`.

1. Daniel's pull requests are [here](https://github.com/MaldonadoJack/cst438-Project1/pulls?q=is%3Apr+author%3Adabarker32)
1. Daniel's Github issues are [here](https://github.com/MaldonadoJack/cst438-Project1/issues?q=is%3Aissue+author%3Adabarker32)

#### What was your role / which stories did you work on

Daniel built the nutrition-facts screen, the login form, account editing, the NutriFind name, and the back buttons. Issues include #12, #13, #19, and #28.

+ What was the biggest challenge?
  + Live nutrition-facts calls were blocked by the FatSecret token while the screen was being connected.
+ Why was it a challenge?
  + Pull request #36 records that API checks could not be finished with the token the project already had.
+ How was the challenge addressed?
  + The screen and mapper were merged with unit tests. Pull Request #47 integrated and fixed API calls.
+ Favorite / most interesting part of this project
  + The most interesting part was building the account-editing screen and seeing how username and password changes were saved in the database and used the next time someone logged in.
+ If you could do it over, what would you change?
  + If I could do it over I would start earlier so I had time to add a favorites system for food items.
+ What is the most valuable thing you learned?
  + The most valuable thing I learned on this project was how to review code better and leave more constructive comments.

### Oscar Aviles-Saldana

1. Oscar's pull requests are [here](https://github.com/MaldonadoJack/cst438-Project1/pulls?q=is%3Apr+author%3AOscarAvilSal)
1. Oscar's Github issues are [here](https://github.com/MaldonadoJack/cst438-Project1/issues?q=is%3Aissue+author%3AOscarAvilSal)

#### What was your role / which stories did you work on

Oscar built the welcome screen, the sign-up screen, the sign-up crash fix, and the path from a successful login to Home. Issues include #22, #7, and #35.

+ What was the biggest challenge?
  + The sign-up screen crashed when a new account was created.
+ Why was it a challenge?
  + Issue #35 blocked a new user from finishing sign-up.
+ How was the challenge addressed?
  + Pull request #37 fixed the crash. Pull request #38 then saved the account, hashed the password, and left the user on the sign-up screen until they logged in.
+ Favorite / most interesting part of this project
  + My Favorite part of the project was watching the functionalities come together to form the final product.
+ If you could do it over, what would you change?
  + I think something I would change is the amount of features the app has.
+ What is the most valuable thing you learned?
  + The most valuable thing I learned was a deeper insight into using GitHub correctly in a team setting.

## Conclusion

- How successful was the project?
  - The demo path is in the app: sign up, log in, search by name, open nutrition facts, add a food to today, see totals, delete a row, edit the account, and log out.
  - Favorites (#5) and past food logs (#14) were not built.
- What was the largest victory?
  - Pull request #45 tied every food-log entry to the signed-in user. Search (#34), nutrition facts (#36), and today's log (#47) could then work as one app. The database upgrade added the log without deleting existing accounts.
- Final assessment of the project
  - NutriFind can demonstrate today's log for one local account.
  - The app can look up a food and see nutritional info and save to account.

## Setup

Android calorie tracker for CST438. You create an account on the device, search the FatSecret food database, open nutrition facts, and save foods to a daily log stored only on that device.

The GitHub repo is `cst438-Project1`. The Android package is `com.example.donorproject`. On the phone or emulator the icon is labeled **NutriFind**.

### What you can do

1. Sign up or log in. Usernames are case-sensitive (`Alex` and `alex` are different). Any non-blank password is accepted. Passwords are stored as PBKDF2 hashes, not plain text.
2. Search for a food. The search runs after you type at least 2 characters.
3. Tap a result to open nutrition facts, then add that serving to today's food log.
4. Open **Food Logs** to see today's entries, calorie and macro totals, and delete a row.
5. Open **Account** to change the username or password.
6. Tap **logout** to return to the login screen.

There is no saved login session. Closing the app and opening it again shows the welcome screen. Accounts and food logs stay in a local database on the device (`calorie_tracker.db`). They are not uploaded anywhere.

### What you need before you build

- Git
- Android Studio (current stable is fine). It includes the JDK this project uses (Java 21).
- Android SDK Platform 37. Android Studio can install this on first open.
- A phone or emulator running **Android 15 (API 35) or newer**. Older devices cannot install the app (`minSdk` is 35).
- A [FatSecret Platform API](https://platform.fatsecret.com/) account, a registered application, and a current OAuth 2 access token. Food search will not work without it. Sign-up, login, and the food log still work offline.

### 1. Clone the repo

```bash
git clone https://github.com/MaldonadoJack/cst438-Project1.git
cd cst438-Project1
```

Open that folder in Android Studio (**File → Open**). Let Gradle sync finish. If Android Studio asks to install a missing SDK, accept it. Sync writes `local.properties` with your SDK path. That file is not committed.

### 2. Get a FatSecret access token

The app does not ask for a key at runtime. You paste a token into source before you build.

1. Register at [platform.fatsecret.com](https://platform.fatsecret.com/) and create an application. Copy the **Client ID** and **Client Secret**. The secret is shown only once.
2. In the FatSecret application settings, allowlist the public IP address of every network that will call the API:
   - the computer that requests the token
   - the Wi-Fi the emulator uses (usually that same computer)
   - a physical phone's network, if you install on a phone (cellular and Wi-Fi can be different IPs)

   Find the public IP by opening [https://ifconfig.me](https://ifconfig.me) in a browser on that network. If the IP changes, add the new one. A token that works for one teammate fails for another when that person's IP is not on the list.

3. From a computer whose IP is allowlisted, request a token. Replace the placeholders with your Client ID and Client Secret:

   ```bash
   curl -u "YOUR_CLIENT_ID:YOUR_CLIENT_SECRET" \
     -d "grant_type=client_credentials&scope=basic" \
     -X POST https://oauth.fatsecret.com/connect/token
   ```

   A successful response looks like this:

   ```json
   {
     "access_token": "eyJ...",
     "token_type": "Bearer",
     "expires_in": 86400
   }
   ```

   `expires_in` is 86400 seconds (24 hours). Request a new token before it expires.

4. Open `app/src/main/java/com/example/donorproject/FatSecretConfig.kt` and set `ACCESS_TOKEN` to the word `Bearer`, a space, and the `access_token` value:

   ```kotlin
   object FatSecretConfig {
       const val ACCESS_TOKEN = "Bearer eyJ..."
   }
   ```

   Do not commit a personal Client Secret. The access token in this file is what the app sends. Replace it when search starts failing after about a day.

Official token steps: [FatSecret OAuth 2.0](https://platform.fatsecret.com/docs/guides/authentication/oauth2).

### 3. Run the app

#### From Android Studio

1. Wait until Gradle sync succeeds.
2. **Device Manager → Create Device**. Pick a phone and a system image of **API 35 or higher** (API 36 is fine). Start that emulator. A physical phone on Android 15+ also works; enable USB debugging and accept the computer.
3. In the run configuration dropdown, select **app**.
4. Click **Run**.

The first screen is **Welcome**, with **Sign Up** and **Login**.

#### From the command line

Use Android Studio's JDK if `java -version` is not 21. On macOS that JDK is usually:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

Build and install a debug build (an emulator or phone must already be connected):

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Then open **NutriFind** on the device. If `./gradlew` is not executable, run `sh ./gradlew` with the same task name.

`local.properties` must exist and contain `sdk.dir` before a command-line build. Opening the project in Android Studio once creates it. You can also set `ANDROID_HOME` to your Android SDK directory.

### 4. Check that it works

1. Tap **Sign Up**, create a username and password, then go to **Login** and sign in.
2. On Home, type a food name such as `apple`. Results should appear.
3. Tap a result, then **Add to today's food log**.
4. Tap **Food Logs** and confirm the food and the nutrition totals are listed.

If search shows `Search failed: Attempt to invoke virtual method 'java.util.List ...' on a null object reference`, FatSecret returned an error instead of foods. The usual causes are an expired token or a public IP that is not allowlisted. Request a new token and confirm the IP, then rebuild and run again.

### Tests

Unit tests do not need a device:

```bash
./gradlew :app:testDebugUnitTest
```

Instrumented tests need a running emulator or device:

```bash
./gradlew :app:connectedDebugAndroidTest
```

### Stack

- Kotlin, Jetpack Compose, Material 3
- Room for accounts and the food log (database version 2)
- Retrofit and Gson for FatSecret (`https://platform.fatsecret.com/`)
- Gradle 9.6, Android Gradle Plugin 9.4, Java 21
```
