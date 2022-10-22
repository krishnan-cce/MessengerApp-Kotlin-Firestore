package com.example.mygoals.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.AddDairyActivity
import com.example.mygoals.LoginActivity
import com.example.mygoals.MessageChatActivity
import com.example.mygoals.R
import com.example.mygoals.model.Dairy
import com.example.mygoals.model.User
import com.example.mygoals.utils.Constants.USER
import com.example.mygoals.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_add_dairy.*

class DairyAdapter (
     mContext: Context,
     mDairyList: List<Dairy>,

) : RecyclerView.Adapter<DairyAdapter.ViewHolder?>() {
    private val mContext: Context
    private val mDairyList: List<Dairy>

    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mDairyList = mDairyList
        this.mContext = mContext

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dairy_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dairyList = mDairyList[position]
        holder.show_dairy_header?.text = dairyList.header
        holder.show_dairy_content?.text = dairyList.text
        holder.show_dairy_date?.text = dairyList.date.toString()

        //Picasso.get().load(dairyList.image).into(holder.show_dairy_image)
        publisherInfo(holder.show_dairy_publisher!!, dairyList.userId)
        GlideLoader(mContext).loadUserPicture(dairyList.image!!, holder.show_dairy_image!!)

        println(dairyList.image)
        println(dairyList.text)


        holder.show_dairy_header!!.setOnClickListener {
            val intentUpdate = Intent(mContext, AddDairyActivity::class.java)
            intentUpdate.putExtra("dairyId", dairyList.userId)
            mContext.startActivity(intentUpdate)
        }
        holder.show_dairy_image!!.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Delete Dairy",
                "Update Dairy",
                "Send Message",
                "Log Out"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0)
                {
                    deleteDairy(dairyList.dairyId)
                }
                if (position == 1)
                {
                    val intentUpdate = Intent(mContext, AddDairyActivity::class.java)
                    intentUpdate.putExtra("dairyId", dairyList.dairyId)
                    intentUpdate.putExtra("dairyHeader", dairyList.header)
                    intentUpdate.putExtra("dairyText", dairyList.text)
                    intentUpdate.putExtra("dairyImage", dairyList.image)
                    mContext.startActivity(intentUpdate)
                }
                if (position == 2)
                {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", dairyList.userId)
                    mContext.startActivity(intent)
                }
                if (position == 3)
                {
                    dialog.dismiss()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(mContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(intent)

                }
            })
            builder.show()
        }

    }

    override fun getItemCount(): Int {
        return mDairyList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var show_dairy_image: CircleImageView? = null
        var show_dairy_date: TextView? = null
        var show_dairy_content: TextView? = null
        var show_dairy_header: TextView? = null
        var show_dairy_publisher: TextView? = null
        init {

            show_dairy_image = itemView.findViewById(R.id.show_dairy_image)
            show_dairy_date = itemView.findViewById(R.id.show_dairy_date)
            show_dairy_content = itemView.findViewById(R.id.show_dairy_content)
            show_dairy_header = itemView.findViewById(R.id.show_dairy_header)
            show_dairy_publisher = itemView.findViewById(R.id.show_dairy_publisher)

        }

    }
    private fun publisherInfo(userNameTV: TextView, publisher: String)
    {

        val usersRef = FirebaseFirestore.getInstance().collection(USER).document(publisher)

        usersRef.get().addOnSuccessListener{documentSnapshot ->

            val user: User? = documentSnapshot.toObject<User>(User::class.java)
            userNameTV.text = user!!.username


        }.addOnFailureListener {
            /* Toast.makeText(mContext, it.toString(), Toast.LENGTH_LONG).show()*/

        }
    }
    private fun deleteDairy(dairyId: String){

        FirebaseFirestore.getInstance().collection("Dairy")
            .document(dairyId).delete()
    }

}