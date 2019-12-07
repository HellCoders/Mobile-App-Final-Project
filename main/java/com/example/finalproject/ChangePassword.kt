package com.example.finalproject

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.security.MessageDigest

class ChangePassword : AppCompatActivity()
{
    var logins = ArrayList<LogIns>()
    var LogInManager: LogInHelper? = null // A reference to the database

    // Number of incorrect attempts
    var incorrAttempts = 0
    var UNfPWnf = 0

    // Username, password, and email fields
    private lateinit var UNtext : EditText
    private lateinit var NewPWtext : EditText
    private lateinit var OldPWtext : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Change Your Password?")
        setContentView(R.layout.change_password)

        // Getting context for login manager
        LogInManager = LogInHelper(this)

        // Obtaining previously stored logins
        logins.addAll(LogInManager!!.getAllLogIns)

        // Setting up fields for the username, password, and email
        UNtext = findViewById<EditText>(R.id.UserName)
        OldPWtext = findViewById<EditText>(R.id.PassWord)
        OldPWtext.setTransformationMethod(PasswordTransformationMethod())
        NewPWtext = findViewById<EditText>(R.id.NewPassWord)
        NewPWtext.setTransformationMethod(PasswordTransformationMethod())

        // To change password
        val changepw = findViewById<Button>(R.id.Update)
        changepw.setOnClickListener({
            val textFromUN = UNtext.getText().toString()
            var textFromNewPW = NewPWtext.getText().toString()
            textFromNewPW = toMD5Hash(textFromNewPW)
            var textFromOldPW = OldPWtext.getText().toString()
            textFromOldPW = toMD5Hash(textFromOldPW)

            var found = 0
            var foundId = 0.toLong()
            var foundEmail = ""
            var foundUsername = ""
            var foundPictures = ArrayList<PictureHashes>()

            for (i in 0..(logins.size - 1)) {
                if (logins[i].UserName == textFromUN && logins[i].PassWord == textFromOldPW) {
                    found = 1
                    foundEmail = logins[i].Email
                    foundUsername = logins[i].UserName
                    foundId = logins[i].Id
                    foundPictures = logins[i].Pictures
                    break
                }
            }

            var errEmail = ""
            for (j in 0..(logins.size - 1)) {
                if (logins[j].UserName == textFromUN) {
                    UNfPWnf = 1
                    errEmail = logins[j].Email
                    break
                }
            }

            if (found == 1) {
                val changeLogin = LogIns(foundId, foundUsername, textFromNewPW, foundEmail, foundPictures)
                LogInManager!!.updatePassword(changeLogin)
                Mailer.sendMail(foundEmail,
                    "Camera Encryption App - Password Updated",
                    "Your password has been updated"
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            Toast.makeText(this@ChangePassword, "Password updated!", Toast.LENGTH_SHORT).show()
                        }, {
                            Log.e("Email Error", it.toString())
                        })
                this.finish()
            } else if (UNfPWnf == 1) {
                incorrAttempts = incorrAttempts + 1
                if (incorrAttempts == 5) {
                    Mailer.sendMail(errEmail,
                        "Camera Encryption App - Repeated Attempts to Change Your Password",
                        "Someone's trying to change your password! Is it you?"
                    ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Toast.makeText(this@ChangePassword, "Someone's trying to change your password! Is it you?", Toast.LENGTH_SHORT).show()
                            }, {
                                Log.e("Email Error", it.toString())
                            })
                    incorrAttempts = 0
                } else {
                    val existMessage = Toast.makeText(this, "Incorrect Password!", Toast.LENGTH_SHORT)
                    existMessage.show()
                }
                UNfPWnf = 0
            } else {
                incorrAttempts = incorrAttempts + 1
                val existMessage = Toast.makeText(this, "Incorrect Password and/or Username!", Toast.LENGTH_SHORT)
                existMessage.show()
            }
        })
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
}