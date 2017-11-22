package com.x.leo.apphelper.widget
import android.content.Context
import android.graphics.*
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import com.x.leo.apphelper.R


/**
 * @作者:XJY
 * @创建日期: 2017/11/17 14:07
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
class SecretEditText(ctx: Context, attributeSet: AttributeSet?) : EditText(ctx, attributeSet) {
    private var hasBorder: Boolean = false
    private var borderWidth: Int = 5
    private var borderColor: Int = Color.BLACK
    private var spanCount: Int = 6
    private var spanSize: Int = 200
    private var radius = 50
    private var caverLastChar: Boolean = false

    init {
        if (attributeSet != null) {
            val attrs = ctx.obtainStyledAttributes(attributeSet, R.styleable.SecretEditText)
            hasBorder = attrs.getBoolean(R.styleable.SecretEditText_hasBorder, false)
            if (hasBorder) {
                borderColor = attrs.getColor(R.styleable.SecretEditText_sborderColor, Color.BLACK)
                borderWidth = attrs.getDimensionPixelSize(R.styleable.SecretEditText_sborderWidth, 2)
            }
            spanCount = attrs.getInteger(R.styleable.SecretEditText_spanCount, 6)
            spanSize = attrs.getDimensionPixelSize(R.styleable.SecretEditText_spanSize, 200)
            radius = attrs.getDimensionPixelSize(R.styleable.SecretEditText_sradius,20)
            attrs.recycle()
        }
        maxEms = spanCount
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == spanCount) {
                    postDelayed(Runnable {
                        caverLastChar = true
                    }, 1000)
                }else{
                    caverLastChar = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }


    private var lminWidth: Int = 0
    private var lminHeight: Int = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        lminWidth = paddingLeft + paddingRight + spanCount * spanSize
        lminHeight = paddingBottom + paddingTop + spanSize
        val resultWidth = if (widthSize < lminWidth) {
            if (widthMode == MeasureSpec.EXACTLY) {
                throw IllegalArgumentException("too small width set or too big spanSize set")
            } else {
                lminWidth
            }
        } else{
            if(widthMode == MeasureSpec.AT_MOST){
                Log.d("SecretEditText","width mode:AT_MOST" )
                lminWidth
            }else {
                Log.d("SecretEditText","width mode:UNSPEC")
                widthSize
            }
        }
        val resultHeight = if (heightSize < lminHeight) {
            if (heightMode == MeasureSpec.EXACTLY) {
                throw IllegalArgumentException("too small height set or too big spanSize set")
            } else {
                lminHeight
            }
        } else{
            if(heightMode == MeasureSpec.AT_MOST){
                Log.d("SecretEditText","height mode:AT_MOST" )
                lminHeight
            }else{
                Log.d("SecretEditText","height mode:UNSPEC" )
                heightSize
            }
        }
      //  Log.d("SecretEditText","width:" + widthSize + ";height:" + heightSize + "\nminWidth:" + lminWidth + ";minHeight:" + lminHeight
      //  + "\nresultWidth:" + resultWidth + ";resultHeight:" + resultHeight)
        super.onMeasure(MeasureSpec.makeMeasureSpec(resultWidth,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(resultHeight,MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    private var originPoint: Point = Point(paddingLeft, paddingTop)
    override fun onDraw(canvas: Canvas?) {
        background?.draw(canvas)
//        if (lminHeight < height) {
//            if (gravity.and(Gravity.TOP) != Gravity.TOP) {
//                if (gravity.and(Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL ||  gravity.and(Gravity.CENTER) == Gravity.CENTER) {
//                    originPoint.y = height / 2 - lminHeight / 2 + paddingTop
//                } else {
//                    originPoint.y = height  - lminHeight + paddingTop
//                }
//            }
//        }
//
//        if (lminWidth < width) {
//            if (gravity.and(Gravity.LEFT) != Gravity.LEFT) {
//                if (gravity.and(Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL || gravity.and(Gravity.CENTER) == Gravity.CENTER) {
//                    originPoint.x = width / 2 - lminWidth / 2 + paddingLeft
//                } else {
//                    originPoint.x = width  - lminWidth + paddingLeft
//                }
//            }
//        }
      //  Log.d("SercetEditText",originPoint.toString())
        if (hasBorder) {
            drawBorders(canvas)
        }
        if (text != null) {
            paint.textAlign = Paint.Align.CENTER
            resetTextDiff(paint)
            if (text.length >= 2)
                for (i in 0..text.length - 2) {
                    canvas?.drawText(String(charArrayOf(replaceChar)), (originPoint.x  + i * spanSize + spanSize / 2 - textDiff.x).toFloat(), (originPoint.y + spanSize / 2 - textDiff.y).toFloat(), paint)
                }
            if (text.length >= 1)
                canvas?.drawText(String(charArrayOf(if (caverLastChar) replaceChar else text[text.length - 1])), (originPoint.x + (text.length - 1) * spanSize + spanSize / 2 - textDiff.x).toFloat(), (originPoint.y + spanSize / 2 - textDiff.y).toFloat(), paint)

        }
    }

    private val textDiff = PointF(0f,0f)
    private fun resetTextDiff(paint: TextPaint) {
        textDiff.y = -paint.textSize / 2
    }

    private val replaceChar = '*'
    private val lPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val roundRect = RectF()
    private fun drawBorders(canvas: Canvas?) {
        lPaint.color = borderColor
        lPaint.strokeWidth = borderWidth.toFloat()
        lPaint.style = Paint.Style.STROKE
        roundRect.left = originPoint.x.toFloat()
        roundRect.right = (originPoint.x + spanSize * spanCount).toFloat()
        roundRect.top = originPoint.y.toFloat()
        roundRect.bottom = (originPoint.y + spanSize).toFloat()
      //  Log.d("SecretEditText",roundRect.toString())
        canvas?.drawRoundRect(roundRect,  radius.toFloat()
                , radius.toFloat(), lPaint)
        for (i in 1..spanCount - 1) {
            canvas?.drawLine((originPoint.x + i * spanSize).toFloat(), originPoint.y.toFloat(), (originPoint.x + i * spanSize).toFloat(), (originPoint.y + spanSize).toFloat(), lPaint)
        }
    }
}