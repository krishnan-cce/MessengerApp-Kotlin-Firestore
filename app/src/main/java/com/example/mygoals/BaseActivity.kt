package com.example.mygoals


import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mygoals.utils.Constants

import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_progress.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


open class BaseActivity : AppCompatActivity() {
    private val mFireStore = FirebaseFirestore.getInstance()
    open var firebaseUser: FirebaseUser? = null
    val sdf = SimpleDateFormat("MMMM-dd-yyy h:mm a")
    val currentDate = sdf.format(Date())

    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnce = false

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else if(!errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }


    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.tv_progress_text.text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }


    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    fun doubleBackToExit() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()


    }
     fun updateStatus(status: String, lastSeen: Long)
    {
        val ref = FirebaseFirestore.getInstance().collection(Constants.USER).document(firebaseUser!!.uid)


        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        hashMap["lastseen"] = lastSeen
        ref.update(hashMap)
    }

}

/*
    fun Toast.showCustomToast(message: String, activity: Activity, errorMessage: Boolean) {
        val layout = activity.layoutInflater.inflate(
            R.layout.toast_layout,
            activity.findViewById(R.id.toast_container)
        )
        if (errorMessage) {

            val imageView = layout.findViewById<ImageView>(R.id.imageView_toast)
            val textView = layout.findViewById<TextView>(R.id.toast_text)
            //imageView?.setImageIcon(R.drawable.ic_delete_forever_24)
            textView.text = message
            card_view_toast.setCardBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.errorRed
                )
            )
        } else {
            val textView = layout.findViewById<TextView>(R.id.toast_text)
            textView.text = message
            card_view_toast.setCardBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.successGreen
                )
            )
        }
        this.apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }
}*/
