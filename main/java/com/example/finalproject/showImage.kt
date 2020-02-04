package com.example.finalproject

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class showImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = getIntent()
        val name = intent.getStringExtra("PicName")
        val hash = intent.getStringExtra("PicHash")
        val add = intent.getStringExtra("PicLoc")
        val username = intent.getStringExtra("UN")

        setTitle(name)
        setContentView(R.layout.show_image)

        val img = findViewById<View>(R.id.singleIV) as ImageView
        val bm = (img.getDrawable() as BitmapDrawable).getBitmap()
        if (bm != null) {
            bm.recycle()
        }
        img.setImageBitmap(BitmapFactory.decodeFile(hash))

        val backButton = findViewById<View>(R.id.backfromImg) as Button
        backButton.setOnClickListener{
            val intent = Intent(this, PictureScreen::class.java)
            intent.putExtra("UN", username)
            startActivity(intent)
        }
    }
}