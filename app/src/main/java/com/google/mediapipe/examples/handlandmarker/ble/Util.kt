package com.google.mediapipe.examples.handlandmarker.ble

import android.widget.Toast
import com.google.mediapipe.examples.handlandmarker.MyApplication

class Util {
    companion object{
        fun showNotification(msg: String){
            Toast.makeText(MyApplication.applicationContext(),msg,Toast.LENGTH_SHORT).show()
        }
    }
}