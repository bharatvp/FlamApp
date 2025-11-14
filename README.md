# FlamApp - Real-Time Edge Detection Viewer (Complete Project Scaffold)

## Goal
Real-time Android app that captures camera frames, processes them using OpenCV (C++) via JNI, and renders using OpenGL ES.
Includes a TypeScript web viewer to display a sample processed frame.

## What I generated for you
- A full Android Studio project (module `app/`) with Camera2 capture, JNI bridge, native C++ processing stubs using OpenCV APIs,
  and an OpenGL ES renderer (GLSurfaceView + Renderer).
- A `scripts/fetch_opencv.sh` script that attempts to download the OpenCV Android SDK and place native `.so` files into `app/src/main/jniLibs`.
- A `web/` folder that contains a TypeScript-based static viewer to load a base64 PNG of a processed frame.
- Gradle files and CMake files configured to build the native library (you must run the setup script to download OpenCV or adjust CMakeLists to point to OpenCV on your machine).
- A CI workflow is **not** included. You must push the repo to GitHub yourself (I cannot push for you).

## Important Notes (must read)
- I cannot include OpenCV's prebuilt `.so` libraries due to size and licensing. The included `scripts/fetch_opencv.sh` attempts to download OpenCV for you — run it locally to populate `app/src/main/jniLibs/<arch>/`.
- After downloading OpenCV you can open `FlamApp_full/app` in Android Studio and build/run on a physical device (recommended).
- Package name is `com.flamapp.demo` throughout. If you change it, update native JNI signatures and CMake accordingly.

## Quick start
1. Unzip the project: `unzip FlamApp_full.zip`
2. Run the setup script (on a machine with internet): `bash scripts/fetch_opencv.sh`
3. Open Android Studio -> Open an existing project -> select `FlamApp_full/app`
4. Let Gradle sync; install NDK/CMake if prompted.
5. Build & run on a device (grant camera permissions when prompted).
6. To see the web viewer: compile the TS (`cd web && tsc`) and open `web/index.html` in browser.

## What to do if build fails
- If CMake cannot find OpenCV, edit `app/CMakeLists.txt` and set `OpenCV_DIR` to your downloaded OpenCV SDK `sdk/native/jni` dir.
- If you prefer, copy `OpenCV-android-sdk/sdk/native/libs/<arch>/*.so` into `app/src/main/jniLibs/<arch>/` manually.

## Files of interest
- `app/src/main/java/com/flamapp/demo/MainActivity.kt` — Camera2 + UI + GLSurfaceView setup
- `app/src/main/java/com/flamapp/demo/JNIInterface.kt` — JNI wrapper
- `app/src/main/cpp/edge_detector.cpp` — OpenCV processing (Canny)
- `app/src/main/cpp/native-lib.cpp` — native helper
- `app/CMakeLists.txt` — native build config
- `web/main.ts` — TypeScript viewer that loads base64 image from `web/processed_base64.txt`

## When ready to submit
- Initialize a GitHub repo and push this project's root.
- Ensure commit history shows meaningful steps (you can use the commits already included in this scaffold).
- Add Screenshots/GIFs to README and include the demo video link if you record one.

