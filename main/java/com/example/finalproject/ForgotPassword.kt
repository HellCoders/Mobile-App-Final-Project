package com.example.finalproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ForgotPassword : AppCompatActivity()
{
    var logins = ArrayList<LogIns>()
    var LogInManager: LogInHelper? = null // A reference to the database

    // Username and email fields
    private lateinit var UNtext : EditText
    private lateinit var Etext : EditText

    // To lock user for 5 minutes if they attempted incorrectly over and over
    private var IncorrAttmptTime = 0.toLong()
    private var currTime = 0.toLong()
    private var error = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Forgot Your Password?")
        setContentView(R.layout.forgot_password)

        // Getting context for login manager
        LogInManager = LogInHelper(this)

        // Obtaining previously stored logins
        logins.addAll(LogInManager!!.getAllLogIns)

        // Setting up fields for the username and email
        UNtext = findViewById<EditText>(R.id.UserName)
        Etext = findViewById<EditText>(R.id.Email)

        // To obtain your password from your email
        val getpw = findViewById<Button>(R.id.PassWordRequest)
        getpw.setOnClickListener({
            currTime = System.currentTimeMillis()

            val textFromUN = UNtext.getText().toString()
            val textFromEmail = Etext.getText().toString()

            var found = 0
            var foundEmail = ""
            var foundPassword = ""

            if ((currTime - IncorrAttmptTime) <= 300000) {
                val existMessage = Toast.makeText(this, "Locked out! Wait before attempting again", Toast.LENGTH_SHORT)
                existMessage.show()
            } else {
                for (i in 0..(logins.size - 1)) {
                    if (logins[i].UserName == textFromUN || logins[i].Email == textFromEmail) {
                        found = 1
                        foundEmail = logins[i].Email
                        foundPassword = logins[i].PassWord
                        break
                    }
                }

                if (found == 1) {
                    Mailer.sendMail(foundEmail,
                        "Camera Encryption App - Lost Password",
                        "Your password is " + foundPassword
                    ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Toast.makeText(this@ForgotPassword, "Password sent! Check email", Toast.LENGTH_SHORT).show()
                            }, {
                                Log.e("Email Error", it.toString())
                            })
                    this.finish()
                } else {
                    error = error + 1
                    if (error == 5) {
                        IncorrAttmptTime = System.currentTimeMillis()
                        error = 0
                    }
                    val existMessage = Toast.makeText(this, "Account not found!", Toast.LENGTH_SHORT)
                    existMessage.show()
                }
            }
        })
    }
}