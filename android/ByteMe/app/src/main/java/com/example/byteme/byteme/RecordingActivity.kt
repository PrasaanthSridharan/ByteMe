package com.example.byteme.byteme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class RecordingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)
    }

    fun showFlagToast(view: View) {
        Toast
        .makeText(applicationContext, "Flag!", Toast.LENGTH_SHORT)
        .show();
    }
}
