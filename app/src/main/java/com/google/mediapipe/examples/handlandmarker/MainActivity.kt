package com.google.mediapipe.examples.handlandmarker

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mediapipe.examples.handlandmarker.databinding.ActivityMainBinding
import com.google.mediapipe.examples.handlandmarker.ble.BleConnectDialogFragment
import com.google.mediapipe.examples.handlandmarker.ble.BleViewModel
import com.google.mediapipe.examples.handlandmarker.ble.BleRepository
import com.google.mediapipe.examples.handlandmarker.ble.BleViewModelFactory
import java.util.Locale
import androidx.lifecycle.ViewModelProvider
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import android.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var bleViewModel: BleViewModel
    private val messageList = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var bleConnectDialogFragment: BleConnectDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)

        val repository = BleRepository() // 생성자 매개변수에 맞게 조정
        val factory = BleViewModelFactory(repository)
        bleViewModel = ViewModelProvider(this, factory).get(BleViewModel::class.java)

        bleConnectDialogFragment = BleConnectDialogFragment(bleViewModel)

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
        val btnOpenPopup: ImageButton = findViewById(R.id.btnOpenPopup)

        btnOpenPopup.setImageResource(R.drawable.ic_bluetooth_disconnected)

        btnOpenPopup.setOnClickListener {
            showPopup()
        }

        observeViewModel()

        sendButton.setOnClickListener {
            handleSendMessageByYou(messageEditText)
        }



    }

    private fun observeViewModel() {
        bleViewModel.isConnected.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { isConnected ->
                if (isConnected) {
                    activityMainBinding.btnOpenPopup.setImageResource(R.drawable.ic_bluetooth_connected)
                } else {
                    activityMainBinding.btnOpenPopup.setImageResource(R.drawable.ic_bluetooth_disconnected)
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

    fun handleSendMessage(messageEditText: EditText, sender: String) {
        val recyclerView = activityMainBinding.listRv

        // Initialize MessageAdapter
        if (recyclerView?.adapter == null) {
            messageAdapter = MessageAdapter(this, messageList)
            recyclerView?.layoutManager = LinearLayoutManager(this)
            recyclerView?.adapter = messageAdapter
        }

        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            addMessage(message, sender)
            messageEditText.text.clear()
            recyclerView?.scrollToPosition(messageList.size - 1) // Scroll to the new message
        }
        if (sender == "You") {
            sendData(message)
        }

    }

    fun handleSendMessageByBraille(messageEditText: EditText) {
        handleSendMessage(messageEditText, "Braille")
    }

    fun handleSendMessageByYou(messageEditText: EditText) {
        handleSendMessage(messageEditText, "You")
    }

    private fun sendData(data: String) {
        val bleviewModel = ViewModelProvider(this).get(BleViewModel::class.java)

        bleviewModel.writeData(data, "string")
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

    private fun showPopup() {
        val fragmentManager = supportFragmentManager
        bleConnectDialogFragment.show(fragmentManager, "BleConnectDialog")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.key_settings -> {
                val intent = Intent(this, ShortcutKeySetting::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("앱을 종료하시겠습니까?")
            .setCancelable(false)
            .setPositiveButton("예") { dialog, id ->
                super.onBackPressed()
                finishAffinity()
            }
            .setNegativeButton("아니요") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onBackPressed() {
        // Handle back press in the activity

        if (bleConnectDialogFragment.isVisible) {
            bleConnectDialogFragment.dismiss()
            // Optionally handle back press logic for the dialog
            // For example, you can call a function inside BleConnectDialog to handle it
        } else {
            showExitConfirmationDialog()
        }
    }
}
