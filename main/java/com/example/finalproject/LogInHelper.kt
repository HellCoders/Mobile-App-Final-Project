package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LogInHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object
    {
        private val DATABASE_NAME = "LogInsDB"
        private val DATABASE_VERSION = 2

        // Log In Table
        private val LOGINS = "LOGINS"
        private val ID = "ID"
        private val USERNAME = "USERNAME"
        private val PASSWORD = "PASSWORD"
        private val EMAIL = "EMAIL"

        // Picture Hashes Table
        private val PICTURES = "PICTURES"
        private val PICTURE_ID = "PICTURE_ID"
        private val USER_ID = "USER_ID"
        private val PICTURE_HASH = "PICTURE_HASH"
        private val ADDRESS = "ADDRESS"
        private val DATE_TIME = "DATE_TIME"

        // The Tables
        private val LOGINS_TABLE = ("CREATE TABLE " + LOGINS + "(" + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERNAME + " TEXT, " +
                PASSWORD + " TEXT, " + EMAIL + " TEXT " + ");")

        private val PICTURE_TABLE = ("CREATE TABLE " + PICTURES + "(" +
                USER_ID + " INTEGER, " + PICTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PICTURE_HASH + " TEXT, " + DATE_TIME + " TEXT, " + ADDRESS + " TEXT " + ");")
    }

    override fun onCreate(p0: SQLiteDatabase)
    {
        p0.execSQL(LOGINS_TABLE)
        p0.execSQL(PICTURE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, oldVer: Int, newVer: Int) {
        p0.execSQL("DROP TABLE IF EXISTS " + LOGINS)
        p0.execSQL("DROP TABLE IF EXISTS " + PICTURES)
        onCreate(p0)
    }

    fun updatePassword(Entry: LogIns)
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USERNAME, Entry.UserName)
        values.put(PASSWORD, Entry.PassWord)
        values.put(EMAIL, Entry.Email)
        db.update(LOGINS, values, "$ID=?", arrayOf(Entry.Id.toString()))
    }

    fun addLogIn(Entry: LogIns)
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USERNAME, Entry.UserName)
        values.put(PASSWORD, Entry.PassWord)
        values.put(EMAIL, Entry.Email)
        Entry.Id = db.insert(LOGINS, null, values)
        db.close()
    }

    fun addPicture(Id: Long, Image: PictureHashes)
    {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_ID, Id)
        values.put(PICTURE_HASH, Image.picture_hash)
        values.put(ADDRESS, Image.Address)
        values.put(DATE_TIME, Image.Date_Time)
        Image.picture_id = db.insert(PICTURES, null, values)
        db.close()
    }

    val getAllLogIns: ArrayList<LogIns>
        get()
        {
            val LogInsArrayList = ArrayList<LogIns>()

            val selectQuery = "SELECT * FROM $LOGINS ORDER BY $ID"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst())
            {
                while (cursor.isAfterLast == false)
                {
                    val L = LogIns()

                    L.Id = cursor.getLong(cursor.getColumnIndex(ID))
                    L.UserName = cursor.getString(cursor.getColumnIndex(USERNAME))
                    L.PassWord = cursor.getString(cursor.getColumnIndex(PASSWORD))
                    L.Email = cursor.getString(cursor.getColumnIndex(EMAIL))

                    val PictureArrayList = ArrayList<PictureHashes>()

                    val Id = L.Id
//                    val secQuery = "SELECT * FROM $PICTURES WHERE $USER_ID = $Id"
                    val secQuery = "SELECT * FROM $PICTURES ORDER BY $PICTURE_ID"
                    val sCursor = db.rawQuery(secQuery, null)
                    if (sCursor.moveToFirst())
                    {
                        while (sCursor.isAfterLast == false)
                        {
                            if (sCursor.getLong(sCursor.getColumnIndex(USER_ID)) == Id)
                            {
                                val PH = PictureHashes()

                                PH.picture_id = sCursor.getLong(sCursor.getColumnIndex(PICTURE_ID))
                                PH.picture_hash = sCursor.getString(sCursor.getColumnIndex(PICTURE_HASH))
                                PH.Address = sCursor.getString(sCursor.getColumnIndex(ADDRESS))
                                PH.Date_Time = sCursor.getString(sCursor.getColumnIndex(DATE_TIME))

                                PictureArrayList.add(PH)
                            }
                            sCursor.moveToNext()
                        }
                    }
                    L.Pictures = PictureArrayList

                    LogInsArrayList.add(L)

                    cursor.moveToNext()
                }
            }
            db.close()
            return LogInsArrayList
        }
}