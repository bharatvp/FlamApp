#!/usr/bin/env bash
set -e
OUT_DIR="$(pwd)/app/src/main/jniLibs"
mkdir -p "$OUT_DIR"
echo "This script attempts to fetch the OpenCV Android SDK and extract native libs into app/src/main/jniLibs/."
echo "You may need to edit OPENCV_URL to a current OpenCV Android SDK zip URL."

OPENCV_URL="https://github.com/opencv/opencv/releases/download/4.7.0/OpenCV-4.7.0-android-sdk.zip"
TMP_ZIP="/tmp/opencv_android_sdk.zip"

echo "Downloading OpenCV from $OPENCV_URL ..."
if command -v wget >/dev/null 2>&1; then
  wget -O "$TMP_ZIP" "$OPENCV_URL"
elif command -v curl >/dev/null 2>&1; then
  curl -L -o "$TMP_ZIP" "$OPENCV_URL"
else
  echo "Please install curl or wget."
  exit 1
fi

echo "Extracting native libs..."
mkdir -p /tmp/opencv_sdk
unzip -o "$TMP_ZIP" -d /tmp/opencv_sdk
# find libs under sdk/native/libs
SDK_ROOT="$(find /tmp/opencv_sdk -maxdepth 2 -type d -name 'sdk' | head -n1)"
if [ -z "$SDK_ROOT" ]; then
  SDK_ROOT="/tmp/opencv_sdk/OpenCV-android-sdk/sdk"
fi
LIBS_DIR="$SDK_ROOT/native/libs"
if [ ! -d "$LIBS_DIR" ]; then
  echo "Couldn't find native/libs under extracted SDK. Inspect /tmp/opencv_sdk"
  exit 1
fi
for ARCH in armeabi-v7a arm64-v8a x86 x86_64; do
  if [ -d "$LIBS_DIR/$ARCH" ]; then
    mkdir -p "$OUT_DIR/$ARCH"
    cp -v "$LIBS_DIR/$ARCH/"*.so "$OUT_DIR/$ARCH/" || true
  fi
done
echo "OpenCV native libs copied to app/src/main/jniLibs/"
echo "You may still need to adjust CMakeLists.txt to point to OpenCV includes if needed."
