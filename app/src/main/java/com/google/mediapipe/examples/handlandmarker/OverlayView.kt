package com.google.mediapipe.examples.handlandmarker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.examples.handlandmarker.ble.BleViewModel
import kotlin.math.abs
import androidx.lifecycle.ViewModelProvider

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()
    private var intersectPaint = Paint()

    private var spaceIntersect = false
    private var enterIntersect = false
    private var backspaceIntersect = false
    private var controlIntersect = false

    private var scaleFactor: Float = 1f
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1
    private var preChar: Char? = null
    private var backnum: Int = 0
    private var changenum: Int = 0
    private var spacenum: Int = 0
    private var enternum: Int = 0
    private var charnum: Int = 0
    private var charcheknum: Int = 20
    private var backview: Boolean = false
    private var changeview: Boolean = false
    private var spaceview: Boolean = false
    private var enterview: Boolean = false
    private var doublebool: Boolean = false
    private var Charview: Boolean = false
    private var Numberview: Boolean = false
    private var EnglishLetterview: Boolean = false
    private var EnglishLetter: Char? = null
    var inputchartype: Int = 0

    private var initialConsonantstemp: Char? = null
    private var medialVowelstemp: Char? = null
    private var finalConsonantstemp: Char? = null
    private var signbool1: Boolean = false
    private var signbool2: Boolean = false
    private var preSign: Char? = null
    private var addSign: Boolean = false

    // 점자 패턴 정의
    var temppatten = listOf(false, false, false, false, false, false)
    private val brailleEnglishupperPatterns = mapOf(
        'A' to listOf(false, false, true, false, false, false),
        'B' to listOf(false, true, true, false, false, false),
        'C' to listOf(false, false, true, true, false, false),
        'D' to listOf(false, false, true, true, true, false),
        'E' to listOf(false, false, true, false, true, false),
        'F' to listOf(false, true, true, true, false, false),
        'G' to listOf(false, true, true, true, true, false),
        'H' to listOf(false, true, true, false, true, false),
        'I' to listOf(false, true, false, true, false, false),
        'J' to listOf(false, true, false, true, true, false),
        'K' to listOf(true, false, true, false, false, false),
        'L' to listOf(true, true, true, false, false, false),
        'M' to listOf(true, false, true, true, false, false),
        'N' to listOf(true, false, true, true, true, false),
        'O' to listOf(true, false, true, false, true, false),
        'P' to listOf(true, true, true, true, false, false),
        'Q' to listOf(true, true, true, true, true, false),
        'R' to listOf(true, true, true, false, true, false),
        'S' to listOf(true, true, false, true, false, false),
        'T' to listOf(true, true, false, true, true, false),
        'U' to listOf(true, false, true, false, false, true),
        'V' to listOf(true, true, true, false, false, true),
        'W' to listOf(false, true, false, true, true, true),
        'X' to listOf(true, false, true, true, false, true),
        'Y' to listOf(true, false, true, true, true, true),
        'Z' to listOf(true, false, true, false, true, true)
    )
    private val brailleEnglishlowerPatterns = mapOf(
        '0' to listOf(false, false, false, false, false, true),
        'a' to listOf(false, false, true, false, false, false),
        'b' to listOf(false, true, true, false, false, false),
        'c' to listOf(false, false, true, true, false, false),
        'd' to listOf(false, false, true, true, true, false),
        'e' to listOf(false, false, true, false, true, false),
        'f' to listOf(false, true, true, true, false, false),
        'g' to listOf(false, true, true, true, true, false),
        'h' to listOf(false, true, true, false, true, false),
        'i' to listOf(false, true, false, true, false, false),
        'j' to listOf(false, true, false, true, true, false),
        'k' to listOf(true, false, true, false, false, false),
        'l' to listOf(true, true, true, false, false, false),
        'm' to listOf(true, false, true, true, false, false),
        'n' to listOf(true, false, true, true, true, false),
        'o' to listOf(true, false, true, false, true, false),
        'p' to listOf(true, true, true, true, false, false),
        'q' to listOf(true, true, true, true, true, false),
        'r' to listOf(true, true, true, false, true, false),
        's' to listOf(true, true, false, true, false, false),
        't' to listOf(true, true, false, true, true, false),
        'u' to listOf(true, false, true, false, false, true),
        'v' to listOf(true, true, true, false, false, true),
        'w' to listOf(false, true, false, true, true, true),
        'x' to listOf(true, false, true, true, false, true),
        'y' to listOf(true, false, true, true, true, true),
        'z' to listOf(true, false, true, false, true, true)
    )
    private val brailleKoreanConsonantsPatterns = mapOf(
        'ㄱ' to listOf(false, false, false, true, false, false),
        'ㄴ' to listOf(false, false, true, true, false, false),
        'ㄷ' to listOf(false, true, false, true, false, false),
        'ㄹ' to listOf(false, false, false, false, true, false),
        'ㅁ' to listOf(false, false, true, false, true, false),
        'ㅂ' to listOf(false, false, false, true, true, false),
        'ㅅ' to listOf(false, false, false, false, false, true),
        'ㅇ' to listOf(false, true, true, true, true, false),
        'ㅈ' to listOf(false, false, false, true, false, true),
        'ㅊ' to listOf(false, false, false, false, true, true),
        'ㅋ' to listOf(false, true, true, true, false, false),
        'ㅌ' to listOf(false, true, true, false, true, false),
        'ㅍ' to listOf(false, false, true, true, true, false),
        'ㅎ' to listOf(false, true, false, true, true, false),
    )
    private val brailleKoreanVowelsPatterns = mapOf(
        'ㅏ' to listOf(false, true, true, false, false, true),
        'ㅑ' to listOf(true, false, false, true, true, false),
        'ㅓ' to listOf(true, true, false, true, false, false),
        'ㅕ' to listOf(false, false, true, false, true, true),
        'ㅗ' to listOf(true, false, true, false, false, true),
        'ㅛ' to listOf(true, false, false, true, false, true),
        'ㅜ' to listOf(true, false, true, true, false, false),
        'ㅠ' to listOf(false, false, true, true, false, true),
        'ㅡ' to listOf(false, true, false, true, false, true),
        'ㅣ' to listOf(true, false, true, false, true, false),
        'ㅐ' to listOf(true, true, true, false, true, false),
        'ㅔ' to listOf(true, false, true, true, true, false),
        'ㅖ' to listOf(true, false, false, true, false, false),
        'ㅘ' to listOf(true, true, true, false, false, true),
        'ㅚ' to listOf(true, false, true, true, true, true),
        'ㅝ' to listOf(true, true, true, true, false, false),
        'ㅢ' to listOf(false, true, false, true, true, true)
    )
    private val brailleKoreanUnderPatterns = mapOf(
        'ㄱ' to listOf(false, false, true, false, false, false),
        'ㄴ' to listOf(false, true, false, false, true, false),
        'ㄷ' to listOf(true, false, false, false, true, false),
        'ㄹ' to listOf(false, true, false, false, false, false),
        'ㅁ' to listOf(false, true, false, false, false, true),
        'ㅂ' to listOf(false, true, true, false, false, false),
        'ㅅ' to listOf(true, false, false, false, false, false),
        'ㅇ' to listOf(true, true, false, false, true, true),
        'ㅈ' to listOf(true, false, true, false, false, false),
        'ㅊ' to listOf(true, true, false, false, false, false),
        'ㅋ' to listOf(true, true, false, false, true, false),
        'ㅌ' to listOf(true, true, false, false, false, true),
        'ㅍ' to listOf(false, true, false, false, true, true),
        'ㅎ' to listOf(true, false, false, false, true, true),
        'ㅆ' to listOf(true, false, false, true, false, false),
    )
    private val braillPatterns = mapOf(
        'N' to listOf(true, false, false, true, true, true),
        '-' to listOf(true, false, false, false, false, true),
    )
    private val braillSign1Patterns = mapOf(
        '.' to listOf(false, true, false, false, true, true),
        '!' to listOf(true, true, false, false, true, false),
        ',' to listOf(false, false, false, false, true, false),
        '1' to listOf(true, true, false, false, false, true),
        '"' to listOf(true, false, false, false, true, true),
        '2' to listOf(false, false, false, false, false, true),
        '\'' to listOf(true, false, false, false, false, false),
        ':' to listOf(false, true, false, false, false, false),
        '6' to listOf(false, false, false, false, true, true),
        ';' to listOf(true, true, false, false, false, false),
    )
    private val braillSign2Patterns = mapOf(
        '?' to listOf(true, true, false, false, false, true),
        '-' to listOf(true, false, false, false, false, true),
    )
    private val brailleNumberPatterns = mapOf(
        '0' to listOf(false, true, false, true, true, false),
        '1' to listOf(false, false, true, false, false, false),
        '2' to listOf(false, true, true, false, false, false),
        '3' to listOf(false, false, true, true, false, false),
        '4' to listOf(false, false, true, true, true, false),
        '5' to listOf(false, false, true, false, true, false),
        '6' to listOf(false, true, true, true, false, false),
        '7' to listOf(false, true, true, true, true, false),
        '8' to listOf(false, true, true, false, true, false),
        '9' to listOf(false, true, false, true, false, false),
    )

    private val brailleShortcutPatterns = mapOf(
        'a' to listOf(false, false, true, false, false, false),
        'b' to listOf(false, true, false, false, false, false),
        'c' to listOf(true, false, false, false, false, false),
        'd' to listOf(false, false, false, true, false, false),
        'e' to listOf(false, false, false, false, true, false),
        'f' to listOf(false, false, false, false, false, true),
        'g' to listOf(false, true, true, false, false, false),
        'h' to listOf(true, false, true, false, false, false),
        'i' to listOf(true, true, false, false, false, false),
        'j' to listOf(true, true, true, false, false, false),
        'k' to listOf(false, false, false, true, true, false),
        'l' to listOf(false, false, false, true, false, true),
        'm' to listOf(false, false, false, false, true, true),
        'n' to listOf(false, false, false, true, true, true),
        'o' to listOf(false, false, true, true, false, false),
        'p' to listOf(false, true, false, true, false, false),
        'q' to listOf(true, false, false, true, false, false),
        'r' to listOf(false, false, true, false, true, false),
        's' to listOf(false, false, true, false, false, true),
        't' to listOf(false, true, true, true, true, false),
    )


    // 임의의 좌표 정의
    private val randomPoints = listOf(
        Triple(0.233f, 0.903f, -0.30f),
        Triple(0.317f, 0.90f, -0.33f),
        Triple(0.393f, 0.853f, -0.32f),
        Triple(0.585f, 0.848f, -0.4f),
        Triple(0.66f, 0.89f, -0.44f),
        Triple(0.745f, 0.888f, -0.4f)
    )
    private val spacePoint = Triple(0.54f, 0.655f, -0.30f)
    private val controlPoint = Triple(0.44f, 0.656f, -0.30f)
    private val enterPoint = Triple(0.832f, 0.845f, -0.30f)
    private val backspacePoint = Triple(0.145f, 0.86f, -0.30f)

    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        intersectPaint.reset()
        invalidate()
        initPaints()
    }

    fun vibrate(context: Context, durationMillis: Long) {
        // Vibrator 객체 생성
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 진동 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 안드로이드 Oreo(26) 이상에서는 VibrationEffect를 사용합니다.
            vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // 안드로이드 Oreo 미만에서는 deprecated된 vibrate 메서드를 사용합니다.
            vibrator.vibrate(durationMillis)
        }
    }
    private fun initPaints() {
        context?.let {
            linePaint.color = ContextCompat.getColor(it, R.color.mp_color_primary)
        }
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL_AND_STROKE

        intersectPaint.color = Color.RED
        intersectPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        intersectPaint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.save()
        canvas.scale(-1f, 1f, width / 2f, height / 2f)

        results?.let { handLandmarkerResult ->
            for (landmark in handLandmarkerResult.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor + offsetX,
                        normalizedLandmark.y() * imageHeight * scaleFactor + offsetY,
                        pointPaint
                    )
                }

                val numOfHands = handLandmarkerResult.landmarks().size
                for (i in 0 until numOfHands) {
                    val handLandmarks = handLandmarkerResult.landmarks()[i]
                    HandLandmarker.HAND_CONNECTIONS.forEach {
                        canvas.drawLine(
                            handLandmarks[it.start()].x() * imageWidth * scaleFactor + offsetX,
                            handLandmarks[it.start()].y() * imageHeight * scaleFactor + offsetY,
                            handLandmarks[it.end()].x() * imageWidth * scaleFactor + offsetX,
                            handLandmarks[it.end()].y() * imageHeight * scaleFactor + offsetY,
                            linePaint
                        )
                    }
                }
            }
            // 교차 여부 판단 및 표시
            val brailleIntersectedPoints = mutableListOf<Boolean>()

            // 모든 점 그리기 및 교차 여부 판단
            val allPoints = randomPoints + listOf(spacePoint, enterPoint, backspacePoint, controlPoint)
            for ((index, point) in allPoints.withIndex()) {
                val pointX = point.first * imageWidth * scaleFactor + offsetX
                val pointY = point.second * imageHeight * scaleFactor + offsetY
                val pointZ = point.third

                var intersects = false
                for (handLandmarks in handLandmarkerResult.landmarks()) {
                    var num = 0
                    for (landmarkIndex in listOf(4, 8, 12, 16, 20)) {
                        val threshold = listOf(18, 25, 18, 27, 20)
                        val landmark = handLandmarks[landmarkIndex]
                        val landmarkX = landmark.x() * imageWidth * scaleFactor + offsetX
                        val landmarkY = landmark.y() * imageHeight * scaleFactor + offsetY
                        val landmarkZ = landmark.z()
                        if (isIntersecting(pointX, pointY, pointZ, landmarkX, landmarkY, landmarkZ, threshold[num])) {
                            intersects = true
                            num++
                            break
                        }else{
                            num++
                        }
                    }
                    if (intersects) break
                }

                if (intersects) {
                    canvas.drawPoint(pointX, pointY, intersectPaint)
                    if (index < randomPoints.size) {  // 점자 패턴 좌표만 추가
                        brailleIntersectedPoints.add(true)
                    }
                } else {
                    canvas.drawPoint(pointX, pointY, pointPaint)
                    if (index < randomPoints.size) {  // 점자 패턴 좌표만 추가
                        brailleIntersectedPoints.add(false)
                    }
                }

                // 비활성화된 점의 교차 여부 저장
                when (point) {
                    spacePoint -> spaceIntersect = intersects
                    enterPoint -> enterIntersect = intersects
                    backspacePoint -> backspaceIntersect = intersects
                    controlPoint -> controlIntersect = intersects
                }
            }

            if(spaceIntersect){
                if (spacenum == charcheknum && !spaceview){
                    val mainActivity = context as MainActivity
                    mainActivity.runOnUiThread {
                        val messageEditText = mainActivity.findViewById<EditText>(R.id.message_et)
                        val currentText = messageEditText.text.toString()
                        val newText = currentText + ' ' // 기존 텍스트에 추가
                        messageEditText.setText(newText)
                        messageEditText.setSelection(newText.length)
                        spaceview = true
                        initialConsonantstemp = null
                        medialVowelstemp = null
                        finalConsonantstemp = null
                        showToastMessage(context, "Space Button")
                    }
                }
                else{
                    spacenum ++
                }
            }
            else{
                spacenum = 0
                spaceview = false
            }
            if(backspaceIntersect){
                if(brailleIntersectedPoints == listOf(false,false,false,false,false,false)) {
                    signbool2 = false
                    if (backnum == charcheknum && !backview){
                        val mainActivity = context as MainActivity
                        mainActivity.runOnUiThread {
                            val messageEditText = mainActivity.findViewById<EditText>(R.id.message_et)
                            val currentText = messageEditText.text.toString()
                            val newText = currentText.dropLast(1) // 기존 텍스트에 추가
                            messageEditText.setText(newText)
                            messageEditText.setSelection(newText.length)
                            showToastMessage(context, "Back Space Button")
                            backview = true
                        }
                    }
                    else{
                        backnum ++
                    }
                }
                else{
                    signbool2 = true
                }
            }
            else{
                backnum = 0
                backview = false
                signbool2 = false
            }
            if(enterIntersect){
                if(brailleIntersectedPoints == listOf(false,false,false,false,false,false)) {
                    signbool1 = false
                    if (enternum == charcheknum && !enterview) {
                        val mainActivity = context as MainActivity
                        mainActivity.runOnUiThread {
                            val messageEditText =
                                mainActivity.findViewById<EditText>(R.id.message_et)
                            mainActivity.handleSendMessageByBraille(messageEditText)
                            showToastMessage(context, "Enter Button")
                            enterview = true
                            initialConsonantstemp = null
                            medialVowelstemp = null
                            finalConsonantstemp = null
                        }
                    } else {
                        enternum++
                    }
                }
                else{
                    signbool1 = true
                }
            }
            else{
                enternum = 0
                enterview = false
                signbool1 = false
            }
            if(controlIntersect){
                if (changenum == charcheknum && !changeview){
                    when (inputchartype) {
                        0 -> {
                            inputchartype = 1
                            EnglishLetterview = false
                            showToastMessage(context, "한국어 모드")
                        }
                        1 -> {
                            inputchartype = 2
                            EnglishLetterview = false
                            showToastMessage(context, "단축키 모드")
                        }
                        2 -> {
                            inputchartype = 0
                            showToastMessage(context, "English Mode")
                        }
                    }
                    initialConsonantstemp = null
                    medialVowelstemp = null
                    finalConsonantstemp = null
                    changeview = true
                }
                else{
                    changenum ++
                }
            }
            else{
                changenum = 0
                changeview = false
            }


            val (recognizedChar, recognizedInt) = checkBraillePattern(brailleIntersectedPoints)
            if (recognizedChar != null) {
                recognizedChar.let {
                    inputCharacter(recognizedChar, recognizedInt)
                }
            } else {
                if(recognizedInt != 10) {
                    charnum = 0
                    temppatten = listOf(false, false, false, false, false, false)
                    preChar = null
                    Charview = false
                }
                else{}
            }
        }

        canvas.restore()
    }

    private fun isIntersecting(
        x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float, threshold: Int): Boolean {
        return abs(x1 - x2) < threshold && abs(y1 - y2) < threshold && abs(z1 - z2) < threshold
    }

    private fun checkBraillePattern(intersectedPoints: List<Boolean>): Pair<Char?, Int> {

        if(!Charview || (temppatten!=intersectedPoints)) {
            if(signbool1){
                for ((char, pattern) in braillSign1Patterns) {
                    var match = true
                    for (i in pattern.indices) {
                        if (pattern[i] != intersectedPoints[i]) {
                            match = false
                            break
                        }
                    }
                    if (match) {
                        temppatten = intersectedPoints
                        return Pair(char, 5)
                    }
                }
            }
            else if(signbool2){
                for ((char, pattern) in braillSign2Patterns) {
                    var match = true
                    for (i in pattern.indices) {
                        if (pattern[i] != intersectedPoints[i]) {
                            match = false
                            break
                        }
                    }
                    if (match) {
                        temppatten = intersectedPoints
                        return Pair(char, 5)
                    }
                }
            }
            else {
                for ((char, pattern) in braillPatterns) {
                    var match = true
                    for (i in pattern.indices) {
                        if (pattern[i] != intersectedPoints[i]) {
                            match = false
                            break
                        }
                    }
                    if (match) {
                        temppatten = intersectedPoints
                        return Pair(char, 4)
                    }
                }
                if (Numberview) {
                    for ((char, pattern) in brailleNumberPatterns) {
                        var match = true
                        for (i in pattern.indices) {
                            if (pattern[i] != intersectedPoints[i]) {
                                match = false
                                break
                            }
                        }
                        if (match) {
                            temppatten = intersectedPoints
                            return Pair(char, 4)
                        }
                    }
                }
                when (inputchartype) {
                    0 -> {
                        if (EnglishLetter != null && EnglishLetterview) {
                            EnglishLetter = null
                            EnglishLetterview = false
                        }
                        if (EnglishLetterview) {
                            for ((char, pattern) in brailleEnglishupperPatterns) {
                                var match = true
                                for (i in pattern.indices) {
                                    if (pattern[i] != intersectedPoints[i]) {
                                        match = false
                                        break
                                    }
                                }
                                if (match) {
                                    temppatten = intersectedPoints
                                    return Pair(char, 0)
                                }
                            }
                        } else {
                            for ((char, pattern) in brailleEnglishlowerPatterns) {
                                var match = true
                                for (i in pattern.indices) {
                                    if (pattern[i] != intersectedPoints[i]) {
                                        match = false
                                        break
                                    }
                                }
                                if (match) {
                                    temppatten = intersectedPoints
                                    return Pair(char, 0)
                                }
                            }
                        }
                    }

                    1 -> {
                        for ((char, pattern) in brailleKoreanConsonantsPatterns) {
                            var match = true
                            for (i in pattern.indices) {
                                if (pattern[i] != intersectedPoints[i]) {
                                    match = false
                                    break
                                }
                            }
                            if (match) {
                                temppatten = intersectedPoints
                                return Pair(char, 1)
                            }
                        }
                        for ((char, pattern) in brailleKoreanVowelsPatterns) {
                            var match = true
                            for (i in pattern.indices) {
                                if (pattern[i] != intersectedPoints[i]) {
                                    match = false
                                    break
                                }
                            }
                            if (match) {
                                temppatten = intersectedPoints
                                return Pair(char, 2)
                            }
                        }
                        for ((char, pattern) in brailleKoreanUnderPatterns) {
                            var match = true
                            for (i in pattern.indices) {
                                if (pattern[i] != intersectedPoints[i]) {
                                    match = false
                                    break
                                }
                            }
                            if (match) {
                                temppatten = intersectedPoints
                                return Pair(char, 3)
                            }
                        }
                    }

                    2 -> {

                        for ((char, pattern) in brailleShortcutPatterns) {
                            var match = true
                            for (i in pattern.indices) {
                                if (pattern[i] != intersectedPoints[i]) {
                                    match = false
                                    break
                                }
                            }
                            if (match) {
                                temppatten = intersectedPoints

                                return Pair(char, 6)
                            }
                        }
                    }

                    else -> {
                    }
                }
            }
            return Pair(null, 0)
        }
        return Pair(null, 10)
    }

    fun showToastMessage(context: Context, message: String) {
        vibrate(context, 500)
        //playDefaultNotificationSound(context)
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)

        val mainActivity = context as MainActivity
        mainActivity.runOnUiThread {
            mainActivity.speakOut(message)
        }
        toast.show()
    }
    fun playDefaultNotificationSound(context: Context) {
        try {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mediaPlayer = android.media.MediaPlayer.create(context, defaultSoundUri)
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun inputCharacter(char: Char, num: Int) {
        if (char == preChar) {
            if (charnum == charcheknum && !Charview) {

                val mainActivity = context as MainActivity
                val bleViewModel = ViewModelProvider(mainActivity).get(BleViewModel::class.java)

                mainActivity.runOnUiThread {
                    val messageEditText = mainActivity.findViewById<EditText>(R.id.message_et)
                    val currentText = messageEditText.text.toString()

                    when (num) {
                        0 -> {
                            if(char == '0'){
                                EnglishLetterview = true
                                showToastMessage(context, "대문자 변환")
                            }else {
                                if(EnglishLetterview){
                                    EnglishLetter = char
                                }
                                val newText = currentText + char // 기존 텍스트에 추가
                                messageEditText.setText(newText)
                                messageEditText.setSelection(newText.length)
                                showToastMessage(context, "$char")
                                sendData(char.toString())
                            }
                            addSign = false
                        }
                        1 -> {
                            medialVowelstemp = null
                            finalConsonantstemp = null
                            var tempchar = char

                            if(doublebool){
                                when (tempchar) {
                                    'ㄱ' -> {tempchar = 'ㄲ'}
                                    'ㄷ' -> {tempchar = 'ㄸ'}
                                    'ㅂ' -> {tempchar = 'ㅃ'}
                                    'ㅅ' -> {tempchar = 'ㅆ'}
                                    'ㅈ' -> {tempchar = 'ㅉ'}
                                }
                                val newText = currentText.dropLast(1) + tempchar // 기존 텍스트에 추가
                                messageEditText.setText(newText)
                                messageEditText.setSelection(newText.length)
                                sendData(tempchar.toString())
                                doublebool = false
                            }
                            else{
                                val newText = currentText + tempchar // 기존 텍스트에 추가
                                messageEditText.setText(newText)
                                messageEditText.setSelection(newText.length)
                                sendData(tempchar.toString())
                            }
                            if(char == 'ㅅ'){
                                doublebool = true
                            }
                            initialConsonantstemp = tempchar
                            showToastMessage(context, "$tempchar")
                            addSign = false
                        }
                        2 -> {
                            doublebool = false
                            var tempchar = char
                            if(initialConsonantstemp != null) {
                                finalConsonantstemp = null
                                if(addSign){
                                    val combinedChar =
                                        combineCharacters(initialConsonantstemp!!, tempchar, null)
                                    val newText = currentText + combinedChar // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    sendData(combinedChar.toString())
                                    medialVowelstemp = tempchar
                                }
                                else{
                                    if (char == 'ㅖ')
                                    {
                                        if(medialVowelstemp != null){
                                            tempchar = 'ㅆ'
                                            val combinedChar =
                                                combineCharacters(initialConsonantstemp!!, medialVowelstemp!!, tempchar)
                                            val newText = currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            finalConsonantstemp = tempchar
                                        }else{
                                            val combinedChar =
                                                combineCharacters(initialConsonantstemp!!, tempchar, null)
                                            val newText = currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            medialVowelstemp = tempchar
                                        }
                                    }
                                    else {
                                        if (medialVowelstemp == 'ㅑ' && char == 'ㅐ') {
                                            tempchar = 'ㅒ'
                                            val combinedChar =
                                                combineCharacters(
                                                    initialConsonantstemp!!,
                                                    tempchar,
                                                    null
                                                )
                                            val newText =
                                                currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            medialVowelstemp = tempchar
                                        } else if (medialVowelstemp == 'ㅘ' && char == 'ㅐ') {
                                            tempchar = 'ㅙ'
                                            val combinedChar =
                                                combineCharacters(
                                                    initialConsonantstemp!!,
                                                    tempchar,
                                                    null
                                                )
                                            val newText =
                                                currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            medialVowelstemp = tempchar
                                        } else if (medialVowelstemp == 'ㅝ' && char == 'ㅐ') {
                                            tempchar = 'ㅞ'
                                            val combinedChar =
                                                combineCharacters(
                                                    initialConsonantstemp!!,
                                                    tempchar,
                                                    null
                                                )
                                            val newText =
                                                currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            medialVowelstemp = tempchar
                                        } else if (medialVowelstemp == 'ㅜ' && char == 'ㅐ') {
                                            tempchar = 'ㅟ'
                                            val combinedChar =
                                                combineCharacters(
                                                    initialConsonantstemp!!,
                                                    tempchar,
                                                    null
                                                )
                                            val newText =
                                                currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                            messageEditText.setText(newText)
                                            messageEditText.setSelection(newText.length)
                                            sendData(combinedChar.toString())
                                            medialVowelstemp = tempchar
                                        } else {
                                            if (medialVowelstemp != null) {
                                                val combinedChar =
                                                    combineCharacters('ㅇ', tempchar, null)
                                                val newText =
                                                    currentText + combinedChar // 기존 텍스트에 추가
                                                messageEditText.setText(newText)
                                                messageEditText.setSelection(newText.length)
                                                sendData(combinedChar.toString())
                                                initialConsonantstemp = 'ㅇ'
                                                medialVowelstemp = tempchar

                                            } else {
                                                val combinedChar =
                                                    combineCharacters(
                                                        initialConsonantstemp!!,
                                                        tempchar,
                                                        null
                                                    )
                                                val newText =
                                                    currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                                messageEditText.setText(newText)
                                                messageEditText.setSelection(newText.length)
                                                sendData(combinedChar.toString())
                                                medialVowelstemp = tempchar
                                            }
                                        }
                                    }
                                }
                            }
                            else{
                                if(addSign){
                                    val combinedChar =
                                        combineCharacters(initialConsonantstemp!!, tempchar, null)
                                    val newText = currentText + combinedChar // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    sendData(combinedChar.toString())
                                    medialVowelstemp = tempchar
                                }
                                else{
                                    if(medialVowelstemp == 'ㅑ' && char == 'ㅐ' ){
                                        tempchar = 'ㅒ'
                                    }else if(medialVowelstemp == 'ㅘ' && char == 'ㅐ' ){
                                        tempchar = 'ㅙ'
                                    }else if(medialVowelstemp == 'ㅝ' && char == 'ㅐ' ){
                                        tempchar = 'ㅞ'
                                    }else if(medialVowelstemp == 'ㅜ' && char == 'ㅐ' ){
                                        tempchar = 'ㅟ'
                                    }
                                    val combinedChar =
                                        combineCharacters('ㅇ', tempchar, null)
                                    val newText = currentText + combinedChar // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    sendData(combinedChar.toString())
                                    initialConsonantstemp = 'ㅇ'
                                    medialVowelstemp = tempchar
                                }
                            }
                            showToastMessage(context, "$tempchar")
                            addSign = false
                        }
                        3 -> {
                            var tempchar = char
                            if(initialConsonantstemp != null && medialVowelstemp != null ) {
                                if(finalConsonantstemp == 'ㄱ'){
                                    when (char) {
                                        'ㄱ' -> {
                                            tempchar = 'ㄲ'
                                        }
                                        'ㅅ' -> {
                                            tempchar = 'ㄳ'
                                        }
                                    }
                                }else if(finalConsonantstemp == 'ㄴ' ){
                                    when (char) {
                                        'ㅈ' -> {
                                            tempchar = 'ㄵ'
                                        }
                                        'ㅎ' -> {
                                            tempchar = 'ㄶ'
                                        }
                                    }
                                }else if(finalConsonantstemp == 'ㄹ' ){
                                    when (char) {
                                        'ㄱ' -> {
                                            tempchar = 'ㄺ'
                                        }
                                        'ㅁ' -> {
                                            tempchar = 'ㄻ'
                                        }
                                        'ㅂ' -> {
                                            tempchar = 'ㄼ'
                                        }
                                        'ㅅ' -> {
                                            tempchar = 'ㄽ'
                                        }
                                        'ㅌ' -> {
                                            tempchar = 'ㄾ'
                                        }
                                        'ㅍ' -> {
                                            tempchar = 'ㄿ'
                                        }
                                        'ㅎ' -> {
                                            tempchar = 'ㅀ'
                                        }
                                    }
                                }else if(finalConsonantstemp == 'ㅂ' && char == 'ㅅ'){
                                        tempchar = 'ㅄ'
                                }else if(finalConsonantstemp == 'ㅅ' && char == 'ㅅ'){
                                    tempchar = 'ㅆ'
                                }
                                val combinedChar =
                                    combineCharacters(initialConsonantstemp!!, medialVowelstemp!!, tempchar)
                                val newText = currentText.dropLast(1) + combinedChar // 기존 텍스트에 추가
                                messageEditText.setText(newText)
                                messageEditText.setSelection(newText.length)
                                sendData(combinedChar.toString())
                                finalConsonantstemp = tempchar
                            }
//                            else{
//                                val newText = currentText + char // 기존 텍스트에 추가
//                                messageEditText.setText(newText)
//                                messageEditText.setSelection(newText.length)
//                            }
                            showToastMessage(context, "$tempchar")
                            addSign = false
                        }
                        4 -> {
                            when(char)
                            {
                                'N' -> {
                                    Numberview = true
                                    showToastMessage(context, "숫자 입력")
                                }
                                '-' -> {
                                    addSign = true
                                    showToastMessage(context, "붙임 표시")
                                }
                                else -> {
                                    addSign = false
                                    Numberview = false
                                    val newText = currentText + char // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    showToastMessage(context, "$char")
                                    sendData(char.toString())
                                }
                            }
                        }
                        5 -> {
                            when(char){
                                '-' -> {
                                    if(preSign == char){
                                        val newText = currentText.dropLast(1) + '~' // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "물결")
                                        sendData(char.toString())
                                        preSign = null
                                    }else{
                                        val newText = currentText + char // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "작대기")
                                        sendData(char.toString())
                                        preSign = char
                                    }
                                }
                                '1' -> {
                                    if(preSign == '2') {
                                        val newText = currentText + '\'' // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "작은 따음표 열기")
                                        sendData(char.toString())
                                        preSign = null
                                    }else {
                                        val newText = currentText + '"' // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "큰 따음표 열기")
                                        sendData(char.toString())
                                        preSign = null
                                    }
                                }
                                '"' -> {
                                    val newText = currentText + char // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    showToastMessage(context, "큰 따음표 닫기")
                                    sendData(char.toString())
                                    preSign = '4'
                                }
                                '2' -> {
                                    preSign = char
                                    showToastMessage(context, "작은 따음표 열기 첫번째 점자")
                                }
                                '\'' -> {
                                    if(preSign == '4') {
                                        val newText = currentText.dropLast(1) + char // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "작은 따음표 닫기")
                                        sendData(char.toString())
                                        preSign = null
                                    }
                                }
                                ',' -> {
                                    val newText = currentText + char // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    showToastMessage(context, "쉼표")
                                    sendData(char.toString())
                                    preSign = '5'
                                }
                                ':' -> {
                                    if(preSign == '5') {
                                        val newText =  currentText.dropLast(1) + char // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "콜론")
                                        sendData(char.toString())
                                        preSign = null
                                    }
                                }
                                '6' -> {
                                    preSign = char
                                    showToastMessage(context, "세미콜론 첫번째 점자")
                                }
                                ':' -> {
                                    if(preSign == '6') {
                                        val newText = currentText + char // 기존 텍스트에 추가
                                        messageEditText.setText(newText)
                                        messageEditText.setSelection(newText.length)
                                        showToastMessage(context, "세미콜론")
                                        sendData(char.toString())
                                        preSign = null
                                    }
                                }
                                else -> {
                                    Numberview = false
                                    val newText = currentText + char // 기존 텍스트에 추가
                                    messageEditText.setText(newText)
                                    messageEditText.setSelection(newText.length)
                                    showToastMessage(context, "$char")
                                    sendData(char.toString())
                                    preSign = null
                                }
                            }
                            addSign = false
                        }

                        6->{
                            val tempstring = when (char) {
                                in 'a'..'t' -> MyApplication.prefs.getString("text_$char", "")

                                else -> char.toString()
                            }

                            val newText = currentText + tempstring
                            messageEditText.setText(newText)
                            messageEditText.setSelection(newText.length)
                            showToastMessage(context, tempstring)


                            if (bleViewModel.isConnect.get()) {
                                classifyAndDecomposeCharacters(tempstring)
                            }
                        }
                        else -> {
                            val newText = currentText + char // 기존 텍스트에 추가
                            messageEditText.setText(newText)
                            messageEditText.setSelection(newText.length)
                            showToastMessage(context, "$char")
                            sendData(char.toString())
                            addSign = false
                        }
                    }
                }

                Charview = true
            }
            else {
                charnum++
            }
        } else {
            charnum = 0
            preChar = char
            Charview = false
        }
    }
    private fun combineCharacters(consonant: Char, vowel: Char, under: Char?): Char? {
        val initialConsonants = listOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
        val medialVowels = listOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ')
        val finalConsonants  = listOf(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
//        println("consonant: $consonant , vowel: $vowel, under: $under");

        val choIndex = initialConsonants.indexOf(consonant)
        val jungIndex = medialVowels.indexOf(vowel)
        val jongIndex = if (under != null) finalConsonants.indexOf(under) else 0

        if (choIndex == -1 || jungIndex == -1 || jongIndex == -1) return null // Unsupported character

        // 한글 음절 조합 공식: 초성 * 21 * 28 + 중성 * 28 + 종성 + 0xAC00
        val unicodeValue = (choIndex * 21 + jungIndex) * 28 + jongIndex + 0xAC00

        return unicodeValue.toChar()
    }

    private fun decomposeCharacter(syllable: Char): Triple<Char, Char, Char?> {
        val initialConsonants = listOf('ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')
        val medialVowels = listOf('ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ')
        val finalConsonants  = listOf(' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ')

        val unicodeValue = syllable.code - 0xAC00
        val choIndex = unicodeValue / (21 * 28)
        val jungIndex = (unicodeValue % (21 * 28)) / 28
        val jongIndex = unicodeValue % 28

        val initial = initialConsonants[choIndex]
        val medial = medialVowels[jungIndex]
        val final = if (jongIndex != 0) finalConsonants[jongIndex] else null

        return Triple(initial, medial, final)
    }

    fun Char.isPunctuation(): Boolean {
        return this in listOf(
            '.', ',', '!', '?', ';', ':', '-', '~', '\'', '\"'
        )
    }

    fun classifyAndDecomposeCharacters(text: String) {
        for (char in text) {
            when {
                char in '가'..'힣' -> {
                    val (initial, medial, final) = decomposeCharacter(char)
                    println("Character: $char -> 초성: $initial, 중성: $medial, 종성: ${final ?: "없음"}")
                    println("${char}는 한글입니다.")
                    sendData(char.toString())
                    sendData(initial.toString())
                    sendData(medial.toString())
                    if (final != null) {
                        sendData(final.toString())
                    }
                }

                char in 'a'..'z' || char in 'A'..'Z' -> {
                    println("${char}는 영어입니다.")
                    sendData(char.toString())
                }

                char in '0'..'9' -> {
                    println("${char}는 숫자입니다.")
                    sendData(char.toString())
                }

                char.isWhitespace() -> {
                    println("${char}는 공백입니다.")
                    sendData(char.toString())
                }

                char.isPunctuation() -> {
                    println("${char}는 문장부호입니다.")
                    sendData(char.toString())
                }

                else -> println("${char}는 알 수 없는 문자입니다.")
            }
        }
    }

    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandmarkerResults

        for (handIndex in handLandmarkerResults.landmarks().indices) {
            val handLandmarks = handLandmarkerResults.landmarks()[handIndex]
            for (landmarkIndex in listOf(4, 8, 12, 16, 20)) {
                val landmark = handLandmarks[landmarkIndex]
                val x = landmark.x()
                val y = landmark.y()
                val z = landmark.z()
            }
        }

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat() / imageHeight

        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = height.toFloat() / imageHeight
            offsetX = (width - imageWidth * scaleFactor) / 2f
            offsetY = 0f
        } else {
            scaleFactor = width.toFloat() / imageWidth
            offsetX = 0f
            offsetY = (height - imageHeight * scaleFactor) / 2f
        }

        invalidate()
    }

    private fun sendData(data: String) {
        val mainActivity = context as MainActivity
        val bleviewModel = ViewModelProvider(mainActivity).get(BleViewModel::class.java)
        if (bleviewModel.isConnect.get()) {
            bleviewModel.writeData(data, "string")
        }
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}
