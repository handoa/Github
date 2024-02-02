package com.example.github



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.github.adapter.ChatAdapter
import com.example.github.data.User
import com.example.github.data.userId
import com.google.firebase.appcheck.internal.util.Logger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class activity_realtimeTalk : AppCompatActivity(){
    lateinit var toolbar: Toolbar
    lateinit var actionBar: ActionBar
    lateinit var edt: EditText
    lateinit var listView: ListView
    lateinit var adapter: ChatAdapter
    val messageItems: ArrayList<com.example.github.data.Message> = ArrayList()

    lateinit var auth: FirebaseAuth
    lateinit var userRef: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase  //Firebase Database 관리 객체 참조 변수
    lateinit var chatRef: DatabaseReference  //'chat'노드의 참조 객체 참조 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime_talk)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) //액티비티의 앱바로 지정
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 만들기

        edt = findViewById(R.id.edt)
        listView = findViewById(R.id.listview)
        adapter = ChatAdapter(messageItems,layoutInflater)
        listView.adapter = adapter

        //Firebase DB 관리 객체와 'chat'노드 참조객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance()
        chatRef = firebaseDatabase.getReference("chat")
        userRef = firebaseDatabase.getReference("users")

        //firbase에서 채팅 메세지들 실시간 읽어오기
        //'chat'노드에 저장되어 있는 데이터들을 읽어오기
        //chatRef에 데이터가 변경되는 것을 듣는 리스너 추가
        chatRef.addChildEventListener(object : ChildEventListener {
            // 새로 추가된 것만 줌 ValueListener는 하나의 값만 바뀌어도 처음부터 다시 값을 줌
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                // 새로 추가된 데이터(값 : MessageItem객체) 가져오기
                val messageItem = dataSnapshot.getValue(com.example.github.data.Message::class.java)

                // 새로운 메세지를 리스트뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem!!)

                // 리스트뷰를 갱신
                adapter.notifyDataSetChanged()
                listView.setSelection(messageItems.size - 1) // 리스트뷰의 마지막 위치로 스크롤 위치 이동
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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

    //05:00 여기서부터.....
    fun clickSend(view: View) {

        //firbase DB에 저장할 값들
        var msgId: String = userId.userid.toString()
        val message: String = edt.text.toString()

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        userRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                msgId = dataSnapshot.getValue<User>()!!.userId.toString()
                //msgId.value!!.userId.toString()
                Log.d(Logger.TAG, "Value is: $msgId")
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(Logger.TAG, "Failed to read value.", error.toException())
            }
        })

        val calendar: Calendar = Calendar.getInstance() // 현재 시간을 가지고 있는 객체
        val time: String = "${calendar[Calendar.HOUR_OF_DAY]}:${calendar[Calendar.MINUTE]}"

        // Firebase DB에 저장할 값(MessageItem객체) 설정
        val messageItem = com.example.github.data.Message(msgId, message, time)
        // 'char'노드에 MessageItem객체를 통해
        chatRef.push().setValue(messageItem)

        // EditText에 있는 글씨 지우기
        edt.setText("")
    }
}

/*@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
}*/