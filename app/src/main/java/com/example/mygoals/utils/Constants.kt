package com.example.mygoals.utils


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap


object Constants {

    const val  CHATS ="Chats"
    const val CHATLIST = "ChatList"


    const val TIME_STAMP ="timestamp"
    const val USER_IMAGE = "image"

    const val USER_ID ="id"
    const val USER_MARKED ="marked"
    const val EMPLOYEE_LEAVE = "EmployeeLeave"
    const val EMPLOYEE_ACTIVE ="EmployeePresent"
    const val LEAVE_COMMENT ="comment"
    const val ORDER_NAME = "username"

    const val USER = "users"
    const val USER_CATEGORY = "category"
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val EXTRA_USER_DETAILS = "extra_user_details"
    const val USER_NAME = "username"

    const val POSTS = "Posts"


    const val READ_STORAGE_PERMISSION_CODE = 2
    const val USER_PROFILE_IMAGE = "user_profile_image"
    const val IMAGE: String = "image"



    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}