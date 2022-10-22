package com.example.mygoals.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.R
import com.example.mygoals.fragments.ProfileFragment
import com.example.mygoals.model.Cover
import com.example.mygoals.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CoverAdapter (
    mContext: Context,
    mUsers: List<Cover>,

    ) : RecyclerView.Adapter<CoverAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mUsers: List<Cover>
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val color = ContextCompat.getColor(mContext, R.color.successGreen)
    val colorRed = ContextCompat.getColor(mContext, R.color.errorRed)

    init {
        this.mUsers = mUsers
        this.mContext = mContext

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.cover_images_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val cover: Cover = mUsers[i]
        println("Cover" + cover.cover)
        println(cover.cover)

        if (cover.cover == ""){

        }else{
            Picasso.get().load(cover.cover).resize(150,150)
                .placeholder(R.drawable.profile).into(holder.iv_cover_image)
        }
        




    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var iv_cover_image: ImageView

        init {

            iv_cover_image = itemView.findViewById(R.id.iv_cover_image)
        }
    }


}
