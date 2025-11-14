package com.flamapp.demo

class JNIInterface {
    companion object {
        init { System.loadLibrary("native-lib") }
    }

    external fun processGrayFrame(input: ByteArray, width: Int, height: Int): ByteArray
}
