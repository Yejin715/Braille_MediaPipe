package com.google.mediapipe.examples.handlandmarker

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.handlandmarker.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val messageList = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        //activityMainBinding.navigation?.setupWithNavController(navController)
        //activityMainBinding.navigation?.setOnNavigationItemReselectedListener {
        // 재선택 무시 }

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
                // 음성 속도 설정 (0.5 ~ 2.0 사이로 설정 가능)
                textToSpeech.setSpeechRate(0.9f) // 예: 속도 1.5배로 설정
            }
        }

        // MessageAdapter 초기화
        messageAdapter = MessageAdapter(this, messageList)

        val sendButton: Button = findViewById(R.id.submit_btn)
        val messageEditText: EditText = findViewById(R.id.message_et)


        sendButton.setOnClickListener {
            handleSendMessage(messageEditText)
        }

        // 엔터 키 이벤트 처리
        messageEditText.setOnKeyListener { _, keyCode, event ->
            handleKeyDown(keyCode, event, sendButton)
        }

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    // TTS 함수 호출
                }
            }
        })

    }
    public fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    // 액티비티가 소멸될 때 TextToSpeech 객체도 해제
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    public fun handleSendMessage(messageEditText: EditText) {


        val recyclerView = activityMainBinding.listRv

        // MessageAdapter 초기화
        messageAdapter = MessageAdapter(this, messageList)

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = messageAdapter
        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            addMessage(message, "You")
            messageEditText.text.clear()
            recyclerView?.scrollToPosition(messageList.size - 1) // 새로운 메시지가 보이도록 스크롤
        }
    }

    private fun handleKeyDown(keyCode: Int, event: KeyEvent, sendButton: Button): Boolean {
        return if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
            sendButton.performClick()
            true
        } else {
            false
        }
    }

    private fun addMessage(message: String, senderName: String) {
        val timestamp = System.currentTimeMillis()
        val newMessage = Message(message, senderName, timestamp)
        messageList.add(newMessage)
        messageAdapter.notifyItemInserted(messageList.size - 1) // 새로운 아이템 추가를 어댑터에 알림
    }

    override fun onBackPressed() {
        finish()
    }
}
