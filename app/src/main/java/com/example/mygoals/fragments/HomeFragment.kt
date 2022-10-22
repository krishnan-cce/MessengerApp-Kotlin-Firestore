package com.example.mygoals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygoals.R
import com.example.mygoals.adapter.DairyAdapter
import com.example.mygoals.model.Dairy
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : BaseFragment() {
    private var dairyAdapter: DairyAdapter? = null
    private var dairyList: MutableList<Dairy>? = null
    val mFireStore = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null



        recyclerView = view.findViewById(R.id.dairy_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        dairyList = ArrayList()
        dairyAdapter = context?.let { DairyAdapter(it, dairyList as ArrayList<Dairy>) }
        recyclerView.adapter = dairyAdapter


        retrieveDairys()
        return view
    }
    private fun retrieveDairys() {
        val dairyRef = mFireStore.collection("Dairy")

        dairyRef.addSnapshotListener { value, error ->
            error?.let {
                return@addSnapshotListener
            }
            value?.let {
                dairyList?.clear()
                for (document in it.documents) {
                    val dairy: Dairy? = document.toObject<Dairy>(Dairy::class.java)
                    dairyList!!.add(dairy!!)
                    dairyAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }

}