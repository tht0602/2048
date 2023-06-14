package tzu.hsuan.tseng.tooowlforate.safering

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import tzu.hsuan.tseng.tooowlforate.R

class SafeCheckRingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 分母，不得為0
     */
    var denominator = 8 //分母
        set(value) {
            field = value.coerceAtLeast(1)
            if (numerator > denominator) {
                numerator = denominator
            } else {
                resetRingPath()
                invalidate()
            }
        }

    /**
     * 分子，小於等於分母
     */
    var numerator = 0
        set(value) {
            field = value.coerceIn(0, denominator)
            resetRingPath()
            invalidate()
        }

    private val ringScope = RectF()     // 環的繪圖位置
    private var ringRadius = 50f        // 環的半徑
    private var ringStokeWidth = 8f     // 環的厚度
    private var ringBackgroundPadding = 8f     // 環外的白底
    private var shadowLayer = 4f     // 環外的白底

    private var denominatorTextColor = Color.BLACK     // 分母字色
    private var numeratorTextColor = Color.GREEN     // 分子字色
    private var shadowColor = Color.GRAY     // 陰影色
    private var ringBackColor = Color.GRAY     // 環的底色
    private var ringGradientTopColor = Color.GREEN     // 環的漸層起始色
    private var ringGradientBottomColor = Color.BLUE     // 環的漸層終點色

    private val ringPath = Path()   // 環的路徑
    private val ringPaint = Paint() // 環的著色器
    private val fillPaint = Paint() // 白底著色器
    private lateinit var gradient: LinearGradient   // 線性漸層著色器

    companion object {
        const val DEFAULT_RADIUS_DP = 50f     // 環的半徑
        const val DEFAULT_RING_STOKE_WIDTH_DP = 8f    // 環的厚度
        const val DEFAULT_RING_BACKGROUND_PADDING_DP = 8f    // 環的厚度
        const val DEFAULT_SHADOW_LAYER_DP = 4f    // 環的厚度
        const val START_DEGREE = 270f    // 環的起點角度
    }

    init {
        initAttrs(context, attrs)
        val density = context.resources.displayMetrics.density
        ringRadius = DEFAULT_RADIUS_DP * density
        ringStokeWidth = DEFAULT_RING_STOKE_WIDTH_DP * density
        ringBackgroundPadding = DEFAULT_RING_BACKGROUND_PADDING_DP * density
        shadowLayer = DEFAULT_SHADOW_LAYER_DP * density
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val centerX = width / 2f
        val centerY = height / 2f

        ringScope.apply {
            left = centerX - ringRadius
            top = centerY - ringRadius
            right = centerX + ringRadius
            bottom = centerY + ringRadius
        }

        gradient = LinearGradient(
            ringScope.centerX(), ringScope.centerY(), ringScope.centerX(),
            ringScope.centerY() + ringRadius,
            ringGradientTopColor,
            ringGradientBottomColor,
            Shader.TileMode.CLAMP
        )

        ringPaint.apply {
            reset()
            style = Paint.Style.STROKE
            strokeWidth = ringStokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        resetRingPath()

        fillPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            setShadowLayer(shadowLayer, 0f, 0f, shadowColor)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (ringScope.isEmpty) return

        // 畫白色圓底
        canvas.drawCircle(
            ringScope.centerX(),
            ringScope.centerY(),
            ringRadius + ringBackgroundPadding,
            fillPaint
        )

        // 畫灰色環
        ringPaint.shader = null //清掉漸層色
        ringPaint.color = ringBackColor
        canvas.drawCircle(ringScope.centerX(), ringScope.centerY(), ringRadius, ringPaint)

        // 畫漸層色環
        ringPaint.shader = gradient //套用漸層色
        canvas.drawPath(ringPath, ringPaint)

        // 畫字
        // canvas.drawt
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.SafeCheckRingView)

        denominator = a.getInt(R.styleable.SafeCheckRingView_denominator, 8)
        numerator = a.getInt(R.styleable.SafeCheckRingView_numerator, 0)
        denominatorTextColor = a.getColor(R.styleable.SafeCheckRingView_denominatorTextColor, Color.BLACK)
        numeratorTextColor = a.getColor(R.styleable.SafeCheckRingView_numeratorTextColor, Color.GREEN)
        shadowColor = a.getColor(R.styleable.SafeCheckRingView_shadowColor, Color.GRAY)
        ringBackColor = a.getColor(R.styleable.SafeCheckRingView_ringBackColor, Color.GRAY)
        ringGradientTopColor = a.getColor(R.styleable.SafeCheckRingView_ringGradientTopColor, Color.GREEN)
        ringGradientBottomColor = a.getColor(R.styleable.SafeCheckRingView_ringGradientBottomColor, Color.BLUE)

        a.recycle()
    }

    private fun resetRingPath() {
        if (ringScope.isEmpty) return
        ringPath.reset()
        ringPath.addArc(ringScope, START_DEGREE, 360f * numerator.toFloat() / denominator.toFloat())
    }
}