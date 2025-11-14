cmake_minimum_required(VERSION 3.10)
project(native-lib)

add_library(native-lib SHARED native-lib.cpp edge_detector.cpp)

find_library(log-lib log)

# If OpenCV headers/libs are available via jniLibs (copied by setup script), you may need to set OpenCV_DIR
# or include OpenCV headers manually. Adjust the following include path if you have OpenCV SDK unpacked:
# include_directories(${CMAKE_SOURCE_DIR}/../../jni/opencv/sdk/native/jni/include)
# target_link_libraries(native-lib ${OpenCV_LIBS} ${log-lib})

target_link_libraries(native-lib ${log-lib})
