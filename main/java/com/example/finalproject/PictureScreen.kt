package com.example.finalproject

import LogInHelper
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity

class PictureScreen : AppCompatActivity()
{
    private lateinit var adapter : customList
    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = getIntent()
        val name = intent.getStringExtra("UN")
        setTitle("Welcome, " + name)
        setContentView(R.layout.picture_screen)

        var LogInManager: LogInHelper? = null // A reference to the database
        LogInManager = LogInHelper(this)

        var photos = LogInManager!!.getImagesForUser(name)

        adapter = customList(this, photos)
        listView = findViewById<View>(R.id.listView) as ListView
        listView.adapter = adapter

        val deleteButton =
            findViewById<View>(R.id.DeleteButton) as Button
        val logoutButton =
            findViewById<View>(R.id.LogOutButton) as Button
        val addButton = findViewById<View>(R.id.addBtn) as Button
//        val sortButton = findViewById<View>(R.id.SortButton)
//
//        sortButton.setOnClickListener{
//            photos?.sortBy{it?.picture_name}
//            adapter.notifyDataSetChanged()
//        }

        logoutButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, newImage::class.java)
            intent.putExtra("UN", name)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            //find checked items and remove
            var vi: View
            var checkBox: CheckBox
            var txtView: TextView
            var cursor: Cursor
            val intent = Intent(this, PictureScreen::class.java)
            intent.putExtra("UN", name)


            for (i in 0..(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition())) {
                vi = listView.getChildAt(i)
                checkBox = vi.findViewById<View>(R.id.checkBox) as CheckBox
                txtView = vi.findViewById<View>(R.id.picNameLV) as TextView
                if (checkBox.isChecked) { //Toast.makeText(getApplicationContext(), String., Toast.LENGTH_SHORT).show();

                    LogInManager!!.DeleteByName(txtView.text.toString(), name)
                }
            }
            startActivity(intent)
        }

        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val existMessage = Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT)
            existMessage.show()
            val intent3 = Intent(this, showImage::class.java)

            val picturesforUser = LogInManager!!.getImagesForUser(name)
            val imageInfo = picturesforUser!![position]
            val pic_hash = imageInfo!!.getHash()
            val pic_name = imageInfo!!.getName()
            val pic_loc = imageInfo!!.getAdd()

            intent3.putExtra("UN", name)
            intent3.putExtra("PicName", pic_name)
            intent3.putExtra("PicHash", pic_hash)
            intent3.putExtra("PicLoc", pic_loc)
            startActivity(intent3)
        })


    }

//    override fun onResume() {
//        super.onResume()
//        adapter = customList(this, photos)
//        listView = findViewById<View>(R.id.listView) as ListView
//        listView.adapter = adapter
//    }

    override fun onStop() {
        listView.setAdapter(null)
        super.onStop()
    }
}