package com.example.mygoals

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mygoals.fragments.HomeFragment
import com.example.mygoals.utils.Constants
import com.example.mygoals.utils.GlideLoader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_dairy.*
import java.io.IOException

class AddDairyActivity : BaseActivity(), View.OnClickListener {

    val mFireStore = FirebaseFirestore.getInstance()
    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""
    private var dairyHeader = ""
    private var dairyText = ""
    private var dairyImage = ""
    private var dairyId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dairy)

        et_dairy_image.setOnClickListener(this)
        btn_dairy_save.setOnClickListener(this)


        val intent = intent
        dairyHeader = intent.getStringExtra("dairyHeader").toString()
        dairyText = intent.getStringExtra("dairyText").toString()
        dairyImage = intent.getStringExtra("dairyImage").toString()
        dairyId = intent.getStringExtra("dairyId").toString()



    }

    fun fillDetails(){

            et_dairy_header.setText(dairyHeader)
            et_dairy_main.setText(dairyText)
            Picasso.get().load(dairyImage).into(et_dairy_image)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.et_dairy_image -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_dairy_save ->{
               //     if (validateUserProfileDetails()) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {

                            uploadImageToCloudStorage(
                                this,
                                mSelectedImageFileUri
                            )
                        } else {

                            //updateDairy(dairyId)
                        }
                    //}
                }
            }
        }
    }
    private fun addDairy(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val docRef = mFireStore.collection("Dairy").document()

        val dairyMap = HashMap<String, Any?>()
        dairyMap["userId"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
        dairyMap["dairyId"] = docRef.id
        dairyMap["header"] = et_dairy_header.text.toString()
        dairyMap["date"] = FieldValue.serverTimestamp()
        dairyMap["text"] = et_dairy_main!!.text.toString()
        dairyMap["image"] = mUserProfileImageURL

        docRef.set(dairyMap)
            .addOnSuccessListener {
                hideProgressDialog()
                val intent = Intent(this, HomeFragment::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                hideProgressDialog()
            }
    }


    private fun updateDairy(dairyId: String){
        showProgressDialog(resources.getString(R.string.please_wait))
        val dairyMap = HashMap<String, Any?>()
        dairyMap["dairyId"] = dairyId
        dairyMap["header"] = et_dairy_header.text.toString()
        dairyMap["date"] = FieldValue.serverTimestamp()
        dairyMap["text"] = et_dairy_main!!.text.toString()
        dairyMap["image"] = mUserProfileImageURL

        mFireStore.collection("Dairy").document(dairyId).update(dairyMap)
            .addOnSuccessListener {
                hideProgressDialog()
            }
            .addOnFailureListener {
                hideProgressDialog()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {

                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this@AddDairyActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            et_dairy_image
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddDairyActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL

        addDairy()
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "DairyImages"+ System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is AddDairyActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is AddDairyActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }
    }

