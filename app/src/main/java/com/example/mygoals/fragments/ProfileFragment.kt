package com.example.mygoals.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.LoginActivity
import com.example.mygoals.R
import com.example.mygoals.adapter.CoverAdapter
import com.example.mygoals.model.Cover
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_message_list.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.user_list_layout.view.*


class ProfileFragment : Fragment() {

    val dbRef =  FirebaseFirestore.getInstance()
    val currentUser =  FirebaseAuth.getInstance().currentUser?.uid
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private lateinit var profileId: String
    private var mCovers: ArrayList<Cover>? = null
    private var recyclerView: RecyclerView? = null
    private var coverAdapter: CoverAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        val pref = context?.getSharedPreferences("PREFS", android.content.Context.MODE_PRIVATE)
        if (pref != null)
        {
            this.profileId = pref.getString("profileId", "none").toString()
        }else{
            this.profileId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        }
        var recyclerView: RecyclerView? = null
        if (profileId == firebaseUser?.uid)
        {
            view.btn_save_pfl.visibility = View.GONE
            view.iv_profile.setOnClickListener {
                pickImage()
            }

            view.iv_cover.setOnClickListener {
                coverChecker = "cover"
                pickImage()
            }

            view.btn_save_pfl.setOnClickListener {
                updateDetails()
            }
            view.btn_logout_pfl.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        recyclerView = view.findViewById(R.id.rv_photos_pfls)
        val horizontalLayout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = horizontalLayout
        mCovers = ArrayList()
        coverAdapter = context?.let { CoverAdapter(it, mCovers as ArrayList<Cover>) }
        recyclerView.adapter = coverAdapter

        view.rv_photos_pfls?.showShimmer()

        retreiveCovers()
        fillProfileDetails()


        return view

    }

    private fun updateDetails(){
        val HashMap = HashMap<String, Any?>()
        HashMap["username"] = tv_name_pfl.text
        HashMap["bio"] = tv_category_pfl.text
        HashMap["mobile"] = tv_mobile_pfl.text
        HashMap["email"] = tv_email_pfl.text
        //HashMap["category"] = tv_category_pfl.text
        dbRef.collection("users").document(currentUser!!)
            .set(HashMap)
    }

    private fun fillProfileDetails(){
        dbRef.collection("users").document(profileId)
            .get().addOnSuccessListener {
                val user : User? = it.toObject<User>(User::class.java)
                tv_name_pfl.text = user!!.username
                tv_category_pfl.text = user.bio
                tv_mobile_pfl.text = user.mobile
                tv_email_pfl.text = user.email
                //Picasso.get().load(user.image).placeholder(R.drawable.profile_cover).into(iv_profile)
                //Picasso.get().load(user.cover).placeholder(R.drawable.profile).into(iv_cover)
                if (user.image == "" || user.cover == ""){
                    Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).into(iv_profile)
                    Picasso.get().load(R.drawable.profile_cover).placeholder(R.drawable.profile).into(iv_cover)
                }else{
                    Picasso.get().load(user.image).placeholder(R.drawable.profile).into(iv_profile)
                    Picasso.get().load(user.cover).placeholder(R.drawable.profile).into(iv_cover)
                }
            }.addOnFailureListener {
                return@addOnFailureListener
            }
    }


    private fun pickImage()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode  &&  resultCode == Activity.RESULT_OK  &&  data!!.data != null)
        {
            imageUri = data.data
            Toast.makeText(context, "uploading....", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }


    private fun retreiveCovers() {

            FirebaseFirestore.getInstance().collection("covers").document(profileId)
                .collection("cover").get().addOnSuccessListener {

                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            view?.rv_photos_pfls?.hideShimmer()
                            val covers: Cover? = data.toObject<Cover>(Cover::class.java)
              /*                  (mCovers as ArrayList<Cover>).add(covers!!)
                            coverAdapter = context?.let { it1 -> CoverAdapter(it1, mCovers!!) }
                                recyclerView!!.adapter = coverAdapter*/
                            mCovers!!.add(covers!!)
                            coverAdapter!!.notifyDataSetChanged()
                        }


                    }
                }
    }

    private fun uploadImageToDatabase()
    {
        val usersRefrence = FirebaseFirestore.getInstance().collection("users").document(firebaseUser!!.uid)

        val coverRefrence = FirebaseFirestore.getInstance().collection("covers").document(firebaseUser!!.uid)
            .collection("cover").document()
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait....")
        progressBar.show()

        if (imageUri!=null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover")
                    {
                        val mapCoverImgs = HashMap<String, Any>()
                        mapCoverImgs["id"] = coverRefrence.id
                        mapCoverImgs["cover"] = url
                        mapCoverImgs["lastseen"] = System.currentTimeMillis()

                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersRefrence!!.update(mapCoverImg)
                        coverRefrence!!.set(mapCoverImgs)
                        coverChecker = ""
                    }
                    else
                    {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["image"] = url
                        usersRefrence!!.update(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}