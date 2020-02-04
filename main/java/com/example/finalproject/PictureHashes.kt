package com.example.finalproject

import java.io.Serializable

class PictureHashes(var picture_id: Long = 0,
                    var user_id: String = "",
                    var picture_hash: String = "",
                    var Address: String = "",
                    var Date_Time: String = "",
                    var picture_name: String = "")
                    : Serializable
{
    fun getHash(): String{
        return picture_hash
    }

    fun getAdd(): String{
        return Address
    }

    fun getTime(): String{
        return Date_Time
    }

    fun getName(): String{
        return picture_name
    }
}