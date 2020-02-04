package com.example.finalproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class customList(context: Context?, pictures: ArrayList<PictureHashes?>?) : ArrayAdapter<PictureHashes?>(context!!, 0, pictures!!) {
    lateinit var usedBitmap : Bitmap

    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_row, parent, false)
        } else {
            val imageView = convertView as ImageView
            val bm = (imageView.getDrawable() as BitmapDrawable).getBitmap()
            if (bm != null) {
                bm.recycle()
            }
        }

        val picname = convertView!!.findViewById<View>(R.id.picNameLV) as TextView
        val picadd =
            convertView!!.findViewById<View>(R.id.picAddressLV) as TextView
        val picstring =
            convertView!!.findViewById<View>(R.id.picLV) as ImageView
        val pictime =
            convertView!!.findViewById<View>(R.id.picTimeLV) as TextView

        val picHash: PictureHashes? = getItem(pos)

        picname.setText(picHash!!.getName())
        picadd.setText(picHash.getAdd())
        pictime.setText(picHash.getTime())
        usedBitmap = BitmapFactory.decodeFile(picHash.getHash())
        picstring.setImageBitmap(usedBitmap)

//        val imageInfo = picHash.getHash()
//        val reportPicture = BitmapFactory.decodeFile(imageInfo)
//        picstring.setImageBitmap(reportPicture)

        return convertView
    }
}