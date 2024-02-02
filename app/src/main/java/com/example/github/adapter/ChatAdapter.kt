package com.example.github.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.github.R
import com.example.github.data.Message
import com.example.github.data.userId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter(private val message: ArrayList<Message>, private val layoutInflater: LayoutInflater) : BaseAdapter() {

    lateinit var auth: FirebaseAuth
    override fun getCount(): Int {
        return message.size
    }

    override fun getItem(position: Int): Any {
        return message[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        auth = Firebase.auth
        var uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        // 현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        val item = message[position]

        // 재활용할 뷰는 사용하지 않음!!
        var itemView: View? = null

        // 메세지가 내 메세지인지??
        itemView = when {
            item.userId == userId.userid -> layoutInflater.inflate(R.layout.right_text_view, parent, false)
            else -> layoutInflater.inflate(R.layout.left_text_view, parent, false)
        }

        // 만들어진 itemView에 값들 설정
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val tvMsg: TextView = itemView.findViewById(R.id.tvMsg)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        tvId.text = item.userId
        tvMsg.text = item.message
        tvTime.text = item.time

        return itemView
    }
}