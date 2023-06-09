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

class SafeCheckRingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var denominator = 8 //分母
        set(value) {
            field = value.coerceAtLeast(1)
            resetRingPath()
            invalidate()
        }

    var numerator = 0 //分子
        set(value) {
            field = value.coerceIn(0, denominator)
            resetRingPath()
            invalidate()
        }

    private val oval = RectF()
    private var ringRadius = 50f        // 環的半徑
    private var ringStokeWidth = 8f     // 環的厚度
    private var ringBackgroundPadding = 8f     // 環外的白底
    private var shadowLayer = 4f     // 環外的白底

    private val ringPath = Path()   // 環的路徑
    private val ringPaint = Paint() // 環的著色器
    private val fillPaint = Paint() // 白底著色器
    private lateinit var gradient: LinearGradient

    companion object {
        const val DEFAULT_RADIUS_DP = 50f     // 環的半徑
        const val DEFAULT_RING_STOKE_WIDTH_DP = 8f    // 環的厚度
        const val DEFAULT_RING_BACKGROUND_PADDING_DP = 8f    // 環的厚度
        const val DEFAULT_SHADOW_LAYER_DP = 4f    // 環的厚度
        const val START_DEGREE = 270f    // 環的起點角度
    }

    init {
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

        oval.apply {
            left = centerX - ringRadius
            top = centerY - ringRadius
            right = centerX + ringRadius
            bottom = centerY + ringRadius
        }

        gradient = LinearGradient(
            oval.centerX(), oval.centerY(), oval.centerX(),
            oval.centerY() + ringRadius,
            Color.RED,
            Color.BLUE,
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
            setShadowLayer(shadowLayer, 0f, 0f, Color.GRAY)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (oval.isEmpty) return

        // 畫白色圓底
        canvas.drawCircle(
            oval.centerX(),
            oval.centerY(),
            ringRadius + ringBackgroundPadding,
            fillPaint
        )

        // 畫灰色環
        ringPaint.shader = null //清掉漸層色
        ringPaint.color = Color.GRAY
        canvas.drawCircle(oval.centerX(), oval.centerY(), ringRadius, ringPaint)

        // 畫漸層色環
        ringPaint.shader = gradient //套用漸層色
        canvas.drawPath(ringPath, ringPaint)
    }

    private fun resetRingPath() {
        ringPath.reset()
        ringPath.addArc(oval, START_DEGREE, 360f * numerator.toFloat() / denominator.toFloat())
    }
}