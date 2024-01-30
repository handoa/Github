package com.example.github

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class activity_login : AppCompatActivity() {
    lateinit var loginButton: Button
    lateinit var joinTextView: TextView
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var idRememberCheckBox: CheckBox

    lateinit var auth: FirebaseAuth
    lateinit var preference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        joinTextView = findViewById(R.id.joinTextView)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        idRememberCheckBox = findViewById(R.id.idRememberCheckBox)

        auth = Firebase.auth

        initProperty()
        initializeView()
        initializeListener()

        //로그인 버튼 클릭 시 메인화면으로 이동
        /*loginButton.setOnClickListener {
            signIn(idEditText.text.toString(), passwordEditText.text.toString())

            /*var intentToMain = Intent(this, MainActivity::class.java)
            startActivity(intentToMain)*/
        }*/

        //회원가입 버튼 클릭 시 회원가입 페이지로 이동
        joinTextView.setOnClickListener {
            var intentToSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentToSignup)
        }
    }

    fun initProperty() { //초기 변수 세팅
        auth = FirebaseAuth.getInstance()
        preference = getSharedPreferences("setting", MODE_PRIVATE) //로그인 정보 저장용 SharedPreference
    }

    fun initializeView() { //뷰 초기화
        emailEditText.setText(preference.getString("emial", "")) //마지막으로 로그인 한 이메일 세팅
        passwordEditText.setText(preference.getString("password", "")) //마지막으로 로그인 한 패스워드 세팅
    }

    fun initializeListener() { //버튼 클릭 시 리스너 세팅

        loginButton.setOnClickListener { //로그인 버튼 클릭 시 메인화면으로 이동
            signIn(emailEditText.text.toString(), passwordEditText.text.toString())
        }

        joinTextView.setOnClickListener { //회원가입 버튼 클릭 시 회원가입 페이지로 이동
            var intentToSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentToSignup)
        }
    }

    // 로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    // 로그인
    private fun signIn(id: String, password: String) {

        if (id.isNullOrBlank() && password.isNullOrBlank())
            Toast.makeText(this, "아이디 또는 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
        else {
            auth?.signInWithEmailAndPassword(id, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "로그인에 성공 하였습니다.", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        updateUI(user)
                        finish()
                        //moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(baseContext, "로그인에 실패 하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun updateUI(user: FirebaseUser?) { //로그인 성공 시 화면 이동
        if (user != null) {
            try {
                var preference = getSharedPreferences("setting", MODE_PRIVATE).edit()    //이메일 및 패스워드 저장
                preference.putString("email", emailEditText.text.toString())
                preference.putString("password", passwordEditText.text.toString())
                preference.apply()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?){
        if( user!= null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

}
