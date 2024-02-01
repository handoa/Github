package com.example.github.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.github.R
import com.example.github.activity_realtimeTalk
import com.example.github.data.Message
import com.example.github.databinding.LeftTextViewBinding
import com.example.github.databinding.RightTextViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class ChatAdapter(val context: Context,
                  var chatRoomKey: String?,
                  val opponentUid: String?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messages: ArrayList<Message> = arrayListOf()     //메시지 목록
    var messageKeys: ArrayList<String> = arrayListOf()   //메시지 키 목록
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val recyclerView = (context as activity_realtimeTalk).recyclerView_chat   //목록이 표시될 리사이클러 뷰

    init {
        setupMessages()
    }

    fun setupMessages() {
        getMessages()
    }

    fun getMessages() {
        FirebaseDatabase.getInstance().getReference("ChatRoom")
            .child("chatRooms").child(chatRoomKey!!).child("messages")   //전체 메시지 목록 가져오기
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (data in snapshot.children) {
                        messages.add(data.getValue<Message>()!!)         //메시지 목록에 추가
                        messageKeys.add(data.key!!)                        //메시지 키 목록에 추가
                    }
                    notifyDataSetChanged()          //화면 업데이트
                    recyclerView.scrollToPosition(messages.size - 1)    //스크롤 최 하단으로 내리기
                }
            })
    }

    override fun getItemViewType(position: Int): Int {               //메시지의 id에 따라 내 메시지/상대 메시지 구분
        return if (messages[position].userId.equals(myUid)) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {            //메시지가 내 메시지인 경우
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.right_text_view, parent, false)   //내 메시지 레이아웃으로 초기화

                MyMessageViewHolder(RightTextViewBinding.bind(view))
            }

            else -> {      //메시지가 상대 메시지인 경우
                val view =
                    LayoutInflater.from(context)
                        .inflate(R.layout.left_text_view, parent, false)  //상대 메시지 레이아웃으로 초기화
                OtherMessageViewHolder(LeftTextViewBinding.bind(view))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messages[position].userId.equals(myUid)) {       //레이아웃 항목 초기화
            (holder as MyMessageViewHolder).bind(position)
        } else {
            (holder as OtherMessageViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class OtherMessageViewHolder(itemView: LeftTextViewBinding) :         //상대 메시지 뷰홀더
        RecyclerView.ViewHolder(itemView.root) {
        var background = itemView.background
        var txtMessage = itemView.tvChat
        var txtDate = itemView.txtDate

        fun bind(position: Int) {           //메시지 UI 항목 초기화
            var message = messages[position]
            var sendDate = message.time

            txtMessage.text = message.message

            txtDate.text = getDateText(sendDate)

            setShown(position)             //해당 메시지 확인하여 서버로 전송
        }

        fun getDateText(sendDate: String): String {    //메시지 전송 시각 생성

            var dateText = ""
            var timeString = ""
            if (sendDate.isNotBlank()) {
                timeString = sendDate.substring(8, 12)
                var hour = timeString.substring(0, 2)
                var minute = timeString.substring(2, 4)

                var timeformat = "%02d:%02d"

                if (hour.toInt() > 11) {
                    dateText += "오후 "
                    dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
                } else {
                    dateText += "오전 "
                    dateText += timeformat.format(hour.toInt(), minute.toInt())
                }
            }
            return dateText
        }

        fun setShown(position: Int) {          //메시지 확인하여 서버로 전송
            FirebaseDatabase.getInstance().getReference("ChatRoom")
                .child("chatRooms").child(chatRoomKey!!).child("messages")
                .child(messageKeys[position]).child("confirmed").setValue(true)
                .addOnSuccessListener {
                    Log.i("checkShown", "성공")
                }
        }
    }

    inner class MyMessageViewHolder(itemView: RightTextViewBinding) :       // 내 메시지용 ViewHolder
        RecyclerView.ViewHolder(itemView.root) {
        var background = itemView.background
        var txtMessage = itemView.tvChat
        var txtDate = itemView.txtDate

        fun bind(position: Int) {            //메시지 UI 레이아웃 초기화
            var message = messages[position]
            var sendDate = message.time
            txtMessage.text = message.message

            txtDate.text = getDateText(sendDate)
        }

        fun getDateText(sendDate: String): String {        //메시지 전송 시각 생성
            var dateText = ""
            var timeString = ""
            if (sendDate.isNotBlank()) {
                timeString = sendDate.substring(8, 12)
                var hour = timeString.substring(0, 2)
                var minute = timeString.substring(2, 4)

                var timeformat = "%02d:%02d"

                if (hour.toInt() > 11) {
                    dateText += "오후 "
                    dateText += timeformat.format(hour.toInt() - 12, minute.toInt())
                } else {
                    dateText += "오전 "
                    dateText += timeformat.format(hour.toInt(), minute.toInt())
                }
            }
            return dateText
        }
    }
}
    /*val messages: ArrayList<ChatModel> = arrayListOf() //메세지 목록
    val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val recyclerView = (context as activity_realtimeTalk).recyclerview_chat

    init {
        setupMessages()
    }

    fun setupMessages() {
        getMessages()
    }

    fun getMessages() {
        FirebaseDatabase.getInstance().getReference("Chat").child("message")
    }*/

    /*inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvChat: TextView = view.findViewById(R.id.tvChat)
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].userId == myEmail) {
            1 // 내 메시지
        } else {
            2 // 내 메시지가 아님
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutResId = if (viewType == 1) {
            R.layout.right_text_view // 내 메시지
        } else {
            R.layout.left_text_view // 내 메시지가 아님
        }

        val view = LayoutInflater.from(viewGroup.context).inflate(layoutResId, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvChat.text = items[position].message
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //View Holder를 생성하고 View를 붙여줌
    /*override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.right_text_view, parent, false)
                MyMessageViewHolder()
            } else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.left_text_view, parent, false)
                OtherMessageViewHolder()
            }
        }
    }*/*/