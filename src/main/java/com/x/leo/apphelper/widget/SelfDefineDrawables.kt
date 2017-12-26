package com.x.leo.apphelper.widget

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * @作者:My
 * @创建日期: 2017/7/19 9:26
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */
internal class AnimatedFish(var radius: Int) : Drawable() {
    var mPaint: Paint
    var mAngel: Double = 0.0
    var frequence: Double = 2.0
    lateinit var centerPoint: PointF
    lateinit var headPoint: PointF
    val bodyWidth = 1.6 * radius
    val bodyHeight = radius * Math.sin(Math.toRadians(80.0))
    val bodyEndScale = 0.6

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = Color.RED
        mPaint.alpha = 40
        mPaint.style = Paint.Style.FILL
        val valueAnimator = ValueAnimator()
        valueAnimator.setIntValues(-10, 10)
        valueAnimator.setDuration(1 * 1000)
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                val animatedValue = animation!!.animatedValue as Int
                mAngel = animatedValue.toDouble()
                frequence = Math.abs(animatedValue.toDouble() * 0.18 + 0.02)
                invalidateSelf()
            }
        })
        valueAnimator.start()
    }

    override fun draw(canvas: Canvas?) {
        centerPoint = calcuCenter(canvas!!.width, canvas!!.height)
        headPoint = calcuHeadCenter(centerPoint)
        canvas.drawCircle(headPoint.x, headPoint.y, radius.toFloat(), mPaint);

        val leftFinPath = Path()
        leftFinPath.reset()
        val leftFinStartPoint: PointF = calcuLeftFinStart()
        val leftFinEndPoint: PointF = calcuLeftFinEnd(leftFinStartPoint)
        val leftFinControlPoint: PointF = calcuLeftFinControl(leftFinStartPoint, leftFinEndPoint)
        leftFinPath.moveTo(leftFinStartPoint.x, leftFinStartPoint.y)
        leftFinPath.quadTo(leftFinControlPoint.x, leftFinControlPoint.y, leftFinEndPoint.x, leftFinEndPoint.y)
        leftFinPath.lineTo(leftFinStartPoint.x, leftFinStartPoint.y)
        canvas.drawPath(leftFinPath, mPaint)
        val rightFinPath = Path()
        rightFinPath.reset()
        val rightFinStartPoint: PointF = calcuRightFinStart()
        val rightFinEndPoint: PointF = calcuRightFinEnd(rightFinStartPoint)
        val rightFinControlPoint: PointF = calcuRightFinControl(rightFinStartPoint, rightFinEndPoint)
        rightFinPath.moveTo(rightFinStartPoint.x, rightFinStartPoint.y)
        rightFinPath.quadTo(rightFinControlPoint.x, rightFinControlPoint.y, rightFinEndPoint.x, rightFinEndPoint.y)
        rightFinPath.lineTo(rightFinStartPoint.x, rightFinStartPoint.y)
        canvas.drawPath(rightFinPath, mPaint)

        val secondBodyCircleRadius = (bodyEndScale * bodyHeight / Math.sin(Math.toRadians(80.0))).toFloat()
        val secondBodyCirclePoint:PointF = calcuSecondBodyCirciePoint()
        canvas.drawCircle(secondBodyCirclePoint.x,secondBodyCirclePoint.y, secondBodyCircleRadius,mPaint)

        mPaint.alpha = 90
        val secondBodyPath = Path()
        secondBodyPath.reset()
        val secondBodyPoint1:PointF = calcuSecondBodyPoint1()
        val secondBodyPoint2:PointF = calcuSecondBodyPoint2()
        val secondBodyPoint3:PointF = calcuSecondBodyPoint3()
        val secondBodyPoint4:PointF = calcuSecondBodyPoint4()
        secondBodyPath.moveTo(secondBodyPoint1.x,secondBodyPoint1.y)
        secondBodyPath.lineTo(secondBodyPoint2.x,secondBodyPoint2.y)
        secondBodyPath.lineTo(secondBodyPoint3.x,secondBodyPoint3.y)
        secondBodyPath.lineTo(secondBodyPoint4.x,secondBodyPoint4.y)
        secondBodyPath.lineTo(secondBodyPoint1.x,secondBodyPoint1.y)
        canvas.drawPath(secondBodyPath,mPaint)
        mPaint.alpha = 40

        val bodyPath = Path()
        bodyPath.reset()
        val leftTopPoint: PointF = calcuBodyPoint1()
        bodyPath.moveTo(leftTopPoint.x, leftTopPoint.y)
        val rightTopPoint: PointF = calcuBodyPoint2()
        val upControlPoint: PointF = calcuUpControlPoint()
        bodyPath.quadTo(upControlPoint.x, upControlPoint.y, rightTopPoint.x, rightTopPoint.y)
        val rightBottomPoint: PointF = calcuBodyPoint3()
        bodyPath.lineTo(rightBottomPoint.x, rightBottomPoint.y)
        val leftBottomPoint: PointF = calcuBodyPoint4()
        val bottomControlPoint: PointF = calcuBottomControlPoint()
        bodyPath.quadTo(bottomControlPoint.x, bottomControlPoint.y, leftBottomPoint.x, leftBottomPoint.y)
        bodyPath.lineTo(leftTopPoint.x, leftTopPoint.y)
        mPaint.alpha = 90
        canvas.drawPath(bodyPath, mPaint)
    }

    private fun calcuSecondBodyPoint4(): PointF{
        return PointF()
    }

    private fun  calcuSecondBodyPoint3(): PointF{
        return PointF()
    }

    private fun calcuSecondBodyPoint2(): PointF{
        return PointF()
    }

    private fun calcuSecondBodyPoint1(): PointF {
        return PointF()
    }

    private fun calcuSecondBodyCirciePoint(): PointF {
        val hl = bodyWidth + bodyEndScale * bodyHeight / Math.tan(Math.toRadians(80.0))
        return PointF(centerPoint.x - (hl * Math.cos(Math.toRadians(mAngel))).toFloat(),centerPoint.y + (hl * Math.sin(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuRightFinControl(rightFinStartPoint: PointF, rightFinEndPoint: PointF): PointF {
        val xDiff = 1 / 3
        val hl = frequence * 0.8 * radius
        val linePoint = PointF(xDiff * (rightFinStartPoint.x - rightFinEndPoint.x) + rightFinEndPoint.x, xDiff * (rightFinStartPoint.y - rightFinEndPoint.y) + rightFinEndPoint.y)
        return PointF(linePoint.x + (hl * Math.sin(Math.toRadians(mAngel))).toFloat(), linePoint.y + (hl * Math.cos(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuRightFinEnd(rightFinStartPoint: PointF): PointF {
        return PointF(rightFinStartPoint.x - (radius * Math.cos(Math.toRadians(mAngel))).toFloat(), rightFinStartPoint.y + (radius * Math.sin(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuRightFinStart(): PointF {
        val currentAngle = Math.toRadians(70 + mAngel)
        return PointF(headPoint.x - (Math.cos(currentAngle) * 0.9 * radius).toFloat(), headPoint.y + (Math.sin(currentAngle) * 0.9 * radius).toFloat())
    }

    private fun calcuLeftFinControl(leftFinStartPoint: PointF, leftFinEndPoint: PointF): PointF {
        val xDiff = 1 / 3
        val hl = frequence * 0.8 * radius
        val linePoint = PointF(leftFinEndPoint.x + (leftFinStartPoint.x - leftFinEndPoint.x) * xDiff, xDiff * (-leftFinEndPoint.y + leftFinStartPoint.y) + leftFinEndPoint.y)
        return PointF(linePoint.x - (hl * Math.sin(Math.toRadians(mAngel))).toFloat(), linePoint.y - (hl * Math.cos(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuLeftFinEnd(leftFinStartPoint: PointF): PointF {
        return PointF(leftFinStartPoint.x - (Math.cos(Math.toRadians(mAngel)) * radius).toFloat(), leftFinStartPoint.y + (Math.sin(Math.toRadians(mAngel)) * radius).toFloat())
    }

    private fun calcuLeftFinStart(): PointF {
        val currentAngle = Math.toRadians(70 - mAngel)
        return PointF(headPoint.x - (Math.cos(currentAngle) * 0.9 * radius).toFloat(), headPoint.y - (Math.sin(currentAngle) * 0.9 * radius).toFloat())
    }

    private fun calcuBottomControlPoint(): PointF {
        val hl = bodyHeight * 1.2
        val xDiff = 0.2 * radius
        return PointF(centerPoint.x + (hl * Math.sin(Math.toRadians(mAngel))).toFloat() + xDiff.toFloat(), centerPoint.y + (hl * Math.cos(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuBodyPoint4(): PointF {
        val currAngle = Math.toRadians(mAngel) + Math.atan(bodyHeight * bodyEndScale / bodyWidth)
        val hl = Math.sqrt(bodyHeight * bodyHeight * bodyEndScale * bodyEndScale + bodyWidth * bodyWidth)
        return PointF(centerPoint.x - (hl * Math.cos(currAngle)).toFloat(), centerPoint.y + (hl * Math.sin(currAngle)).toFloat())
    }

    private fun calcuBodyPoint3(): PointF {
        val currAngle = Math.toRadians(mAngel) - Math.atan(bodyHeight / bodyWidth)
        val hl = Math.sqrt(bodyHeight * bodyHeight + bodyWidth * bodyWidth)
        return PointF(centerPoint.x + (hl * Math.cos(currAngle)).toFloat(), centerPoint.y - (hl * Math.sin(currAngle)).toFloat())
    }

    private fun calcuUpControlPoint(): PointF {
        val hl = bodyHeight * 1.2
        val xDiff = 0.2 * radius
        return PointF(centerPoint.x - (hl * Math.sin(Math.toRadians(mAngel))).toFloat() - xDiff.toFloat(), centerPoint.y - (hl * Math.cos(Math.toRadians(mAngel))).toFloat())
    }

    private fun calcuBodyPoint2(): PointF {
        val currAngle = Math.toRadians(mAngel) + Math.atan(bodyHeight / bodyWidth)
        val hl = Math.sqrt(bodyHeight * bodyHeight + bodyWidth * bodyWidth)
        return PointF(centerPoint.x + (hl * Math.cos(currAngle)).toFloat(), centerPoint.y - (hl * Math.sin(currAngle)).toFloat())
    }

    private fun calcuBodyPoint1(): PointF {
        val currAngle = Math.toRadians(mAngel) - Math.atan(bodyHeight * bodyEndScale / bodyWidth)
        val hl = Math.sqrt(bodyHeight * bodyHeight * bodyEndScale * bodyEndScale + bodyWidth * bodyWidth)
        return PointF(centerPoint.x - (hl * Math.cos(currAngle)).toFloat(), centerPoint.y + (hl * Math.sin(currAngle)).toFloat())
    }

    private fun calcuCenter(width: Int, height: Int): PointF {
        return PointF(width / 2.toFloat(), height / 2.toFloat())
    }

    private fun calcuHeadCenter(point: PointF): PointF {
        val fl = bodyWidth - radius * Math.cos(Math.toRadians(80.0))
        return PointF(point.x + (fl * Math.cos(Math.toRadians(mAngel))).toFloat()
                , point.y - (fl * Math.sin(Math.toRadians(mAngel))).toFloat())
    }

    override fun getIntrinsicHeight(): Int {
        return radius * 9
    }

    override fun getIntrinsicWidth(): Int {
        return radius * 9
    }

    override fun setAlpha(alpha: Int) {
        this.alpha = alpha
        mPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.colorFilter = colorFilter
        mPaint.colorFilter = colorFilter
    }

}