package com.example.finalproject

import LogInHelper
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class RegisterScreen : AppCompatActivity()
{
    var logins = ArrayList<LogIns>()
    var LogInManager: LogInHelper? = null // A reference to the database

    // Username, password, and email fields
    private lateinit var UNtext : EditText
    private lateinit var PWtext : EditText
    private lateinit var Etext : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Register Screen")
        setContentView(R.layout.register_screen)

        // Getting context for login manager
        LogInManager = LogInHelper(this)

        // Obtaining previously stored logins
        logins.addAll(LogInManager!!.getAllLogIns)

        // Setting up fields for the username, password, and email
        UNtext = findViewById<EditText>(R.id.UserName)
        PWtext = findViewById<EditText>(R.id.PassWord)
        PWtext.setTransformationMethod(PasswordTransformationMethod())
        Etext = findViewById<EditText>(R.id.Email)

        // To register yourself, assuming that your username and/or
        // email are unique
        val register_login = findViewById<Button>(R.id.Registration)
        register_login.setOnClickListener({
            val textFromUN = UNtext.getText().toString()
            var textFromPW = PWtext.getText().toString()
            textFromPW = toMD5Hash(textFromPW)
            val textFromEmail = Etext.getText().toString()

            var found = 0
            var correct = 1

            for (i in 0..(logins.size - 1)) {
                if (logins[i].UserName == textFromUN || logins[i].Email == textFromEmail) {
                    found = 1
                    break
                }
            }

            if (textFromEmail.contains("@") && textFromEmail.contains(".")) {
                correct = 0
            }

            if (correct == 1) {
                val existMessage = Toast.makeText(this, "Invalid email!", Toast.LENGTH_SHORT)
                existMessage.show()
            } else if (found == 0) {
                val newLogIn = LogIns(0, textFromUN, textFromPW, textFromEmail)
                LogInManager!!.addLogIn(newLogIn)
                logins.clear()
                logins.addAll(LogInManager!!.getAllLogIns)
                val existMessage = Toast.makeText(this, "You have been registered", Toast.LENGTH_SHORT)
                existMessage.show()
                this.finish()
            } else {
                val existMessage = Toast.makeText(this, "This user already exists!", Toast.LENGTH_SHORT)
                existMessage.show()
            }
        })
    }
}

// Formats byte array to a hexadecimal string
fun byteArrayToHexString(array: Array<Byte>) : String {
    var stringBuilder = StringBuilder(array.size * 2)

    for (byte in array) {
        val toAppend =
            String.format("%2X", byte).replace(" ", "0")
        stringBuilder.append(toAppend).append("-")
    }

    stringBuilder.setLength(stringBuilder.length - 1)

    return stringBuilder.toString()
}

// Hashes text so that it can be secured
fun toMD5Hash(text: String) : String {
    var hashedText = ""

    try {
        val md5 = MessageDigest.getInstance("MD5")
        val md5HashBytes = md5.digest(text.toByteArray()).toTypedArray()
        hashedText = byteArrayToHexString(md5HashBytes)
    } catch (e: Exception) {
        Log.e("Couldn't Hash: ", text)
    }

    return hashedText
}