package com.example.finalproject

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.os.Bundle
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.location.*
import android.location.LocationManager
import android.location.Location
import android.location.Address
import android.location.Geocoder
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.core.content.getSystemService

class MainActivity : AppCompatActivity(), SensorEventListener
{
    var logins = ArrayList<LogIns>()
    var LogInManager: LogInHelper? = null // A reference to the database

    private lateinit var sensorManager : SensorManager

    // Username and password fields
    private lateinit var UNtext : EditText
    private lateinit var PWtext : EditText

    private var accel = 0.toFloat() // Acceleration
    private var accelCurrent = 0.toFloat()
    private var accelLast = 0.toFloat()

    // Track number of incorrect log in attempts to lock out user for 5 minutes
    private var incorrAttempts = 0
    private var lastIncorrTime = 0.toLong()
    private var condition = 0

    // To prevent multiple shakes from being registered
    // all at once by having a delay of 100 ms
    private var lastTime = 0.toLong()
    private var currTime = 0.toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Log In Screen")
        setContentView(R.layout.activity_main)

        // Getting context for login manager
        LogInManager = LogInHelper(this)

        // Obtaining previously stored logins
        logins.addAll(LogInManager!!.getAllLogIns)

        // Acquiring and registering sensor manager,
        // as well as initializing acceleration values
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        accel = 0.00f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        // Setting up fields for the username and password
        UNtext = findViewById<EditText>(R.id.UserName)
        PWtext = findViewById<EditText>(R.id.PassWord)
        PWtext.setTransformationMethod(PasswordTransformationMethod())

        // Asking user for permission to access camera, Internet, and location
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
        // || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            // android.Manifest.permission.ACCESS_COARSE_LOCATION
        } else {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
            // || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
                // android.Manifest.permission.ACCESS_COARSE_LOCATION
            }
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA))
        // || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),1)
            // android.Manifest.permission.ACCESS_COARSE_LOCATION
        } else {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
            // || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
                // android.Manifest.permission.ACCESS_COARSE_LOCATION
            }
        }

        // To go to the register screen
        val register_login = findViewById<Button>(R.id.Register)
        register_login.setOnClickListener({
            val register_intent = Intent(this, RegisterScreen::class.java)
            startActivity(register_intent)
        })

        // To go the forgotten password screen
        val forgot_pw = findViewById<Button>(R.id.PassWordForgotten)
        forgot_pw.setOnClickListener({
            val forgotpw_intent = Intent(this, ForgotPassword::class.java)
            startActivity(forgotpw_intent)
        })

        // To update the password
        val update_pw = findViewById<Button>(R.id.ChangePassWord)
        update_pw.setOnClickListener({
            val changepw_intent = Intent(this, ChangePassword::class.java)
            startActivity(changepw_intent)
        })
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing here
    }

    // For detecting shakes
    override fun onSensorChanged(event: SensorEvent) {
        currTime = System.currentTimeMillis()
        if ((currTime - lastTime) > 100) {
            lastTime = currTime

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            accelLast = accelCurrent
            accelCurrent = Math.sqrt((x*x + y*y + z*z).toDouble()).toFloat()
            val delta = accelCurrent - accelLast
            accel = accel * 0.9f + delta

            // Shake detected! View encrypted pictures
            if (accel > 8) {
                val textFromUN = UNtext.getText().toString()
                val textFromPW = PWtext.getText().toString()

                logins.clear()
                logins.addAll(LogInManager!!.getAllLogIns)

                var corrInfo = 0

                if ((currTime - lastIncorrTime) <= 300000) {
                    val existMessage = Toast.makeText(this, "Locked out! Wait before attempting log in again", Toast.LENGTH_SHORT)
                    existMessage.show()
                } else {
                    if (condition == 0 && incorrAttempts == 5) {
                        condition = 1
                        lastIncorrTime = System.currentTimeMillis()
                    }

                    for (i in 0..(logins.size - 1)) {
                        if (logins[i].UserName == textFromUN && logins[i].PassWord == textFromPW) {
                            corrInfo = 1
                            incorrAttempts = 0

                            val picture_intent = Intent(this, PictureScreen::class.java)
                            picture_intent.putExtra("UN", textFromUN)
                            picture_intent.putExtra("PW", textFromPW)
                            startActivity(picture_intent)
                        }
                    }

                    if (corrInfo == 0) {
                        incorrAttempts = incorrAttempts + 1
                        if (incorrAttempts == 5) {
                            condition = 0
                        }
                        val existMessage = Toast.makeText(this, "Incorrect Combination of Username and Password", Toast.LENGTH_SHORT)
                        existMessage.show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        logins.clear()
        logins.addAll(LogInManager!!.getAllLogIns)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        logins.clear()
        logins.addAll(LogInManager!!.getAllLogIns)
        super.onPause()
    }
}
