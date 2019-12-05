package com.example.finalproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PictureScreen : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Images")
        setContentView(R.layout.picture_screen)
    }
}