package com.woodgrove.android.ui.utils.codeinput

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.woodgrove.android.R
import java.util.regex.Pattern

class CodeInputLayout : View {
    private lateinit var characters: FixedStack<Char>
    private lateinit var backgrounds: Array<Background?>
    private lateinit var underlinePaint: Paint
    private lateinit var textPaint: Paint
    private var backgroundReduction: Float = 0.toFloat()
    private var backgroundWidth: Float = 0.toFloat()
    private var textSize: Float = 0.toFloat()
    private var textMarginBottom: Float = 0.toFloat()
    private var viewHeight: Float = 0.toFloat()
    private var codeSize: Int = 0
    private var backgroundColorRes: Int = 0
    private var textColor: Int = 0
    private var inputType = InputType.TYPE_CLASS_NUMBER

    private var codeInputLengthChanged: (codeInputSize: Int) -> Unit = {}

    private val defaultCodes = 8
    private val keycode = "KEYCODE_"
    private val keycodePattern = Pattern.compile("$keycode(\\w)")

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyledAttrs: Int) : super(context, attributeSet, defStyledAttrs) {
        init(attributeSet)
    }

    fun setCodeInputLengthChanged(listener: (codeInputLength: Int) -> Unit) {
        this.codeInputLengthChanged = listener
    }

    fun getCodeInputValue(): String {
        var result = ""
        characters.forEach { result += it }
        return result
    }

    fun setCodeInputValue(code: String) {
        var stringBuilder: StringBuilder
        while (characters.size >= code.toCharArray().size && characters.isNotEmpty()) {
            characters.pop()
        }
        for (c in code.toCharArray()) {
            stringBuilder = StringBuilder(keycode)
            stringBuilder.append(c)
            inputText(stringBuilder.toString())
        }
    }

    fun clearCode() {
        characters.clear()
        codeInputLengthChanged(characters.size)
    }

    private fun init(attributeSet: AttributeSet?) {
        initDefaultAttributes()
        initDataStructures()
        initPaint()
        initViewOptions()
    }

    private fun initDefaultAttributes() {
        backgroundWidth = context.resources.getDimension(R.dimen.underline_width)
        backgroundReduction = context.resources.getDimension(R.dimen.section_reduction)
        textSize = context.resources.getDimension(R.dimen.text_size)
        textMarginBottom = context.resources.getDimension(R.dimen.text_margin_bottom)
        backgroundColorRes = context.resources.getColor(R.color.background)
        textColor = context.resources.getColor(R.color.theme_colour_1_darker)
        viewHeight = context.resources.getDimension(R.dimen.view_height)
        codeSize = defaultCodes
    }

    private fun initDataStructures() {
        backgrounds = arrayOfNulls(codeSize)
        characters = FixedStack()
        characters.maxSize = codeSize
    }

    private fun initPaint() {
        underlinePaint = Paint()
        underlinePaint.color = backgroundColorRes
        underlinePaint.style = Paint.Style.FILL
        textPaint = Paint()
        textPaint.textSize = textSize
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
    }

    private fun initViewOptions() {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(((backgroundWidth + backgroundReduction) * defaultCodes).toInt(), viewHeight.toInt(), oldw, oldh)
        initBackgrounds()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = ((backgroundWidth + backgroundReduction) * defaultCodes).toInt()
        val desiredHeight = viewHeight.toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                measuredWidth
            }
            MeasureSpec.AT_MOST -> {
                desiredWidth.coerceAtMost(measuredWidth)
            }
            else -> {
                desiredWidth
            }
        }

        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                measuredHeight
            }
            MeasureSpec.AT_MOST -> {
                desiredHeight.coerceAtMost(measuredHeight)
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(width, height)
    }

    private fun initBackgrounds() {
        for (i in 0 until codeSize) {
            backgrounds[i] = createPath(i, backgroundWidth)
        }
    }

    private fun createPath(position: Int, sectionWidth: Float): Background {
        val fromX = sectionWidth * position.toFloat()
        return Background(fromX, 0f, fromX + sectionWidth, viewHeight)
    }

    private fun showKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        inputMethodManager.viewClicked(this)
    }

    private fun hideKeyBoard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        outAttrs.actionLabel = null
        outAttrs.inputType = inputType
        return BaseInputConnection(this, false)
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }

    override fun onKeyDown(keyCode: Int, keyevent: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL && !characters.isEmpty()) {
            characters.pop()
            codeInputLengthChanged(characters.size)
        }
        return super.onKeyDown(keyCode, keyevent)
    }

    override fun onKeyUp(keyCode: Int, keyevent: KeyEvent): Boolean {
        val text = KeyEvent.keyCodeToString(keyCode)
        return inputText(text)
    }

    override fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent?): Boolean {
        val text = KeyEvent.keyCodeToString(keyCode)
        return inputText(text)
    }

    private fun inputText(text: String): Boolean {
        val matcher = keycodePattern.matcher(text)
        if (matcher.matches()) {
            val character = matcher.group(1)[0]
            if (TextUtils.isDigitsOnly(character.toString())) {
                characters.push(character)
                codeInputLengthChanged(characters.size)
                return true
            }
        }
        return false
    }

    override fun onTouchEvent(motionevent: MotionEvent): Boolean {
        if (motionevent.action == 0) {
            requestFocus()
            showKeyboard()
        }
        return super.onTouchEvent(motionevent)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in backgrounds.indices) {
            val sectionRect = backgrounds[i]!!
            if (i == 0) {
                val fromX = sectionRect.fromX
                val fromY = sectionRect.fromY
                val toX = sectionRect.toX
                val toY = sectionRect.toY
                drawSection(fromX, fromY, toX, toY, canvas)

                if (characters.toArray().size > i && !characters.isEmpty()) {
                    drawCharacter(fromX, toX, characters[i], canvas)
                }
            } else {
                val fromX = sectionRect.fromX + backgroundReduction * i
                val fromY = sectionRect.fromY
                val toX = sectionRect.toX + backgroundReduction * i
                val toY = sectionRect.toY

                drawSection(fromX, fromY, toX, toY, canvas)

                if (characters.toArray().size > i && !characters.isEmpty()) {
                    drawCharacter(fromX, toX, characters[i], canvas)
                }
            }
        }
        invalidate()
    }

    private fun drawSection(fromX: Float, fromY: Float, toX: Float, toY: Float,
                            canvas: Canvas) {
        val paint = underlinePaint
        val rect = RectF(fromX, fromY, toX, toY)
        canvas.drawRoundRect(rect, 15f, 15f, paint)
    }

    private fun drawCharacter(fromX: Float, toX: Float, character: Char, canvas: Canvas) {
        val actualWidth = toX - fromX
        val centerWidth = actualWidth / 2
        val centerX = fromX + centerWidth
        canvas.drawText(character.toString(), centerX, height - textMarginBottom, textPaint)
    }
}
