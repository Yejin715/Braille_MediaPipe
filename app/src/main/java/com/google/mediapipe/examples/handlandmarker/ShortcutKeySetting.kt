package com.google.mediapipe.examples.handlandmarker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ShortcutKeySetting  : AppCompatActivity() {

    private lateinit var prefs: PreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shortcut_keys)

        prefs = PreferenceUtil(this)

        // EditText 배열 초기화
        val editTexts = arrayOf(
            findViewById<EditText>(R.id.edit_text_a),
            findViewById<EditText>(R.id.edit_text_b),
            findViewById<EditText>(R.id.edit_text_c),
            findViewById<EditText>(R.id.edit_text_d),
            findViewById<EditText>(R.id.edit_text_e),
            findViewById<EditText>(R.id.edit_text_f),
            findViewById<EditText>(R.id.edit_text_g),
            findViewById<EditText>(R.id.edit_text_h),
            findViewById<EditText>(R.id.edit_text_i),
            findViewById<EditText>(R.id.edit_text_j),
            findViewById<EditText>(R.id.edit_text_k),
            findViewById<EditText>(R.id.edit_text_l),
            findViewById<EditText>(R.id.edit_text_m),
            findViewById<EditText>(R.id.edit_text_n),
            findViewById<EditText>(R.id.edit_text_o),
            findViewById<EditText>(R.id.edit_text_p),
            findViewById<EditText>(R.id.edit_text_q),
            findViewById<EditText>(R.id.edit_text_r),
            findViewById<EditText>(R.id.edit_text_s),
            findViewById<EditText>(R.id.edit_text_t)
        )

        // 저장된 값 로드
        for (i in editTexts.indices) {
            editTexts[i].setText(prefs.getString("text_${('a' + i)}", ""))
        }

        findViewById<Button>(R.id.button_save).setOnClickListener {
            for (i in editTexts.indices) {
                prefs.setString("text_${('a' + i)}", editTexts[i].text.toString())
            }
            finish()
        }

        findViewById<Button>(R.id.button_cancel).setOnClickListener {
            finish()
        }
    }
}