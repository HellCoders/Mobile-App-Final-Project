package com.example.finalproject

import LogInHelper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class newImage : AppCompatActivity() {
    var picPath: String = ""
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_GALLERY_IMAGE = 2
    var timeStamp: String = ""
    private val TAG = newImage::class.java.getSimpleName()

    private lateinit var fusedLocClient: FusedLocationProviderClient // For FusedLocationProvider API
    private lateinit var locRequest: LocationRequest // Parameters for FusedLocationProvider
    private lateinit var locCallback: LocationCallback // Callback for changes in location
    private lateinit var loc: Location // The current location
    var addressee = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = getIntent()
        val name = intent.getStringExtra("UN")

        var LogInManager: LogInHelper? = null // A reference to the database
        LogInManager = LogInHelper(this)

        setTitle("Welcome, " + name)
        setContentView(R.layout.image_screen)

        // Create location request and callback
        buildLocReq()
        buildLocCallBack()

        // An instance of Fused Location Provider Client
        fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocClient.requestLocationUpdates(locRequest, locCallback, Looper.myLooper())

        val takePic = findViewById<View>(R.id.takePicture) as Button
        val uploadPic = findViewById<View>(R.id.galleryUpload) as Button
        val addBtn = findViewById<View>(R.id.addImage) as Button
        val nameET = findViewById<View>(R.id.image_nameET) as EditText
        val addTV = findViewById<View>(R.id.image_addressTV) as TextView
        val timeTV = findViewById<View>(R.id.image_timeTV) as TextView

        takePic.setOnClickListener(View.OnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        })

        uploadPic.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE)
        })

        addBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, PictureScreen::class.java)
            intent.putExtra("UN", name)
            val newPic = PictureHashes(
                0,
                name,
                picPath,
                addTV.text.toString(),
                timeTV.text.toString(),
                nameET.text.toString()
            )
//            if(picPath == "") {
//                val existMessage = Toast.makeText(this, "No Picture was Taken", Toast.LENGTH_SHORT)
//                existMessage.show()
//            }
//            else {
            LogInManager!!.addPicture(newPic)

            startActivity(intent)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val img = findViewById<View>(R.id.imageView) as ImageView
        val timeTV = findViewById<View>(R.id.image_timeTV) as TextView
        val addTV = findViewById<View>(R.id.image_addressTV) as TextView

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            img.setImageBitmap(imageBitmap)

            timeStamp = SimpleDateFormat("yyyy-MM-dd--HH:mm:ss").format(Date()).toString()
            //timeStamp = SimpleDateFormat("MM/dd/yyyy HH:mm").format(Date()).toString()
            timeTV.setText(timeStamp)

            //set the address here to the TextView
            addTV.setText(addressee)
//            val lat = obtainLatitude()
//            val long = obtainLongitude()
//            val address = getFullAddressFromLatLong(lat,long)
//            addTV.setText(address)

            val reportFilePath = File(getExternalFilesDir(null), "$timeStamp.png")
            picPath = reportFilePath.toString()

            var fos: FileOutputStream
            try {
                fos = FileOutputStream(reportFilePath)
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                if (!reportFilePath.exists()) {
                    reportFilePath.createNewFile()
                }
                fos.close()
            } catch (e: Exception) {
                Log.i("DATABASE", "Problem updating pic", e)
                picPath = ""
            }
        }


        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK) {
            val contentURI = data!!.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

            img.setImageBitmap(bitmap)

            timeStamp = SimpleDateFormat("yyyy-MM-dd--HH:mm:ss").format(Date()).toString()
            //timeStamp = SimpleDateFormat("MM/dd/yyyy HH:mm").format(Date()).toString()
            timeTV.setText(timeStamp)

            //set the address here to the TextView
            addTV.setText(addressee)
//            val lat = obtainLatitude()
//            val long = obtainLongitude()
//            val address = getFullAddressFromLatLong(lat,long)
//            addTV.setText(address)

            val reportFilePath = File(getExternalFilesDir(null), "$timeStamp.png")
            picPath = reportFilePath.toString()

            var fos: FileOutputStream
            try {
                fos = FileOutputStream(reportFilePath)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                if (!reportFilePath.exists()) {
                    reportFilePath.createNewFile()
                }
                fos.close()
            } catch (e: Exception) {
                Log.i("DATABASE", "Problem updating pic", e)
                picPath = ""
            }

        }
    }

    // Obtain address via GeoCoder class
    private fun getAddress(locResult: LocationResult): String {
        var address = ""
        var geoCoder = Geocoder(this, Locale.getDefault())

        var loc1 = locResult.locations.get(locResult.locations.size - 1)

        try {
            var addresses: ArrayList<Address> =
                geoCoder.getFromLocation(loc1.latitude, loc1.longitude, 1) as ArrayList<Address>
            address = addresses.get(0).getAddressLine(0)
        } catch (e: IOException) {
            // e.printStackTrace()
        }

        return address
    }

    // For obtaining location request
    private fun buildLocReq() {
        // Create a location request to store parameters for the requests
        locRequest = LocationRequest.create()

        // Sets priority, interval, and --smallest displacement-- for requests
        locRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locRequest.interval = 5000
        // locRequest.smallestDisplacement = 10f
    }

    // For obtaining location callback
    private fun buildLocCallBack() {
        locCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                addressee = getAddress(p0)
            }
        }
    }
}