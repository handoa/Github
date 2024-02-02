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
    var msgId: String = ""

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


        //firebase에서 유저의 아이디를 읽어오기
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        userRef.child(uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                msgId = dataSnapshot.getValue<User>()!!.userId.toString()
                Log.d(Logger.TAG, "Value is: $msgId")
                //userId 클래스의 userid 값을 firebase에서 불러온 id로 설정
                userId.userid = msgId
                Log.d("user클래스 값 : ", userId.userid!!)
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(Logger.TAG, "Failed to read value.", error.toException())
            }
        })

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
        val message: String = edt.text.toString()

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