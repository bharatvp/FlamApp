#include <jni.h>
#include <vector>
#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/core.hpp>

using namespace cv;

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_flamapp_demo_JNIInterface_processGrayFrame(JNIEnv *env, jobject /* this */,
                                                  jbyteArray arr_, jint width, jint height) {
    jbyte* arr = env->GetByteArrayElements(arr_, NULL);
    // Create Mat header pointing to the grayscale data (Y plane)
    Mat img(height, width, CV_8UC1, (unsigned char*)arr);
    Mat edges;
    Canny(img, edges, 100, 200);

    jbyteArray outArr = env->NewByteArray(width * height);
    env->SetByteArrayRegion(outArr, 0, width * height, (jbyte*)edges.data);
    env->ReleaseByteArrayElements(arr_, arr, 0);
    return outArr;
}
