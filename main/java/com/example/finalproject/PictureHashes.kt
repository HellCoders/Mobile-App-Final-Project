package com.example.finalproject

import java.io.Serializable

class PictureHashes(var picture_id: Long = 0,
                    var picture_hash: String = "",
                    var Address: String = "",
                    var Date_Time: String = "")
                    : Serializable
{
}