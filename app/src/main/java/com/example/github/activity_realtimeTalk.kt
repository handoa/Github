package com.example.github

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.view.textclassifier.ConversationActions
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.github.adapter.ChatAdapter
import com.example.github.data.ChatRoom
import com.example.github.data.User
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class activity_realtimeTalk : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    lateinit var btnSend: Button
    lateinit var edt: EditText
    //lateinit var stEmail: String

    lateinit var recyclerView_chat: RecyclerView
    private lateinit var firebaseDatabase: DatabaseReference
    lateinit var chatRoom: ChatRoom
    lateinit var chatRoomKey: String
    lateinit var opponentUser: User
    lateinit var myUid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_talk)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        initializerProperty()
        initializeView()
        initializeListener()
        setupChatRooms()

        // database 읽기
        /*myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(TAG, "Value is: $value")
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })*/
    }

    fun initializerProperty(){ //변수 초기화
        myUid = FirebaseAuth.getInstance().currentUser?.uid!!  //현재 로그인한 유저 id
        firebaseDatabase = FirebaseDatabase.getInstance().reference!!

        chatRoom = (intent.getSerializableExtra("ChatRoom", ChatRoom::class.java)!!)
        chatRoomKey = intent.getStringExtra("ChatRoomKey")!!
        //opponentUser = (intent.getSerializableExtra("Opponent", User::class.java)!!)  //상대방 유저 정보
    }

    fun initializeView() {  //뷰 초기화
        btnSend = findViewById(R.id.btnSend)
        edt = findViewById(R.id.edt)
        recyclerView_chat = findViewById(R.id.recyclerview_chat)
    }

    fun initializeListener() { //버튼 클릭 시 리스너 초기화
        btnSend.setOnClickListener() {
            putMessage()
        }
    }

    fun setupChatRooms() { //채팅방 목록 초기화 및 표시
        if (chatRoomKey.isNullOrBlank())
            setupChatRoomKey()
        else
            setupRecycler()
    }

    fun setupChatRoomKey() {  //chatRoomKey 없을 경우 초기화 후 목록 초기화
        FirebaseDatabase.getInstance().getReference("ChatRoom").child("chatRooms")
            .orderByChild("users/${opponentUser.userEmail}").equalTo(true)  //tkdeoqkddml Uid가 포함된 목록이 있는지 확인
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) { }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        chatRoomKey = data.key!!  //chatRoomKey 초기화
                        setupRecycler()  //목록 업데이트
                        break
                    }
                }

            })
    }

    fun putMessage() {  //메세지 전송
        try {
            var message = com.example.github.data.Message(myUid, getDateTimeString(), edt.text.toString()) //메세지 정보 초기화
            Log.i("ChatRoomKey", chatRoomKey)
            FirebaseDatabase.getInstance().getReference("ChatRoom").child("chatRooms")
                .child(chatRoomKey).child("messages")  //현재 채팅방에 메세지 추가
                .push().setValue(message).addOnSuccessListener {
                    Log.i("putMessage", "메세지 전송에 성공하였습니다.")
                    edt.text.clear()
                }.addOnCanceledListener {
                    Log.i("putMessage", "메세지 전송에 실패하였습니다.")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("putMessage", "메시지 전송 중 오류가 발생하였습니다.")
        }
    }

    fun getDateTimeString(): String {  //메세지 보낸 시각 정보 반환
        try {
            var localDateTime = LocalDateTime.now()
            localDateTime.atZone(TimeZone.getDefault().toZoneId())
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            return localDateTime.format(dateTimeFormatter).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("getTimeError")
        }
    }

    fun setupRecycler() {  //목록 초기화 및 업데이트
        recyclerView_chat.layoutManager = LinearLayoutManager(this)
        recyclerView_chat.adapter = ChatAdapter(this, chatRoomKey, opponentUser.userEmail)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            android.R.id.home -> {
                //뒤로가기 눌렀을 때
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}