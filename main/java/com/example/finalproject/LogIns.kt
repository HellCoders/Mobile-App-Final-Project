package com.example.finalproject

import java.io.Serializable

class LogIns(var Id: Long = 0, var UserName: String = "",
             var PassWord: String = "", var Email: String = "",
             var Pictures: ArrayList<PictureHashes> = ArrayList<PictureHashes>())
             : Serializable
{
}