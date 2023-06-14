package tzu.hsuan.tseng.tooowlforate.safering

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import tzu.hsuan.tseng.tooowlforate.R
import kotlin.math.abs

class SafeCheckRingAnimateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_ANIMATION_DURATION = 5L       // 動畫轉動單位時間，360度轉1.8秒
        private const val DEFAULT_RADIUS_DP = 50f               // 環的半徑
        private const val DEFAULT_RING_STOKE_WIDTH_DP = 8f      // 環的厚度
        private const val DEFAULT_RING_BACKGROUND_PADDING_DP = 8f    // 環的厚度
        private const val DEFAULT_SHADOW_LAYER_DP = 4f          // 環的厚度

        private const val TEXT_MARGIN_TOP = 39
        private const val NUMERATOR_TEXT_MARGIN_RIGHT = 4
        private const val NUMERATOR_TEXT_MARGIN_BOTTOM = 26

        private const val START_DEGREE = 270f                   // 環的起點角度
    }

    val denominator: Int
        get() = privateDenominator

    val numerator: Int
        get() = privateNumerator

    var text: String = ""
        set(value) {
            field = value
            invalidate()
        }

    private var privateDenominator = 8  //分母
        set(value) {
            field = value.coerceAtLeast(1)
            if (numerator > denominator) {
                privateNumerator = denominator
            }
        }

    private var privateNumerator = 0    //分子
        set(value) {
            field = value.coerceIn(0, denominator)
        }

    private var processDegree = 0      // 動畫角度
        set(value) {
            field = value
            resetRingPath()
        }

    var valueAnimator: ValueAnimator? = null    // 動畫器

    private val ringScope = RectF()     // 環的繪圖位置
    private var ringRadius = 50f        // 環的半徑
    private var ringStokeWidth = 8f     // 環的厚度
    private var ringBackgroundPadding = 8f     // 環外的白底
    private var shadowLayer = 4f        // 環外的白底

    private var denominatorTextColor = Color.BLACK     // 分母字色
    private var numeratorTextColor = Color.GREEN     // 分子字色
    private var shadowColor = Color.GRAY     // 陰影色
    private var ringBackColor = Color.GRAY     // 環的底色
    private var ringGradientTopColor = Color.GREEN     // 環的漸層起始色
    private var ringGradientBottomColor = Color.BLUE     // 環的漸層終點色

    private var textBottom = 0f
    private var textNumeratorRight = 0f
    private var textNumeratorBottomLine = 0f
    private var textDenominatorSlashLeft = 0f
    private var textDenominatorLeft = 0f
    private var textDenominatorBottomLine = 0f

    private val ringProgressPath = Path()   // 環的路徑
    private val ringPaint = Paint() // 環的著色器
    private val textPaint = Paint() // 字串的著色器
    private val textNumeratorPaint = Paint() // 分子字串的著色器
    private val textDenominatorPaint = Paint() // 分子母字串的著色器
    private val fillPaint = Paint() // 白底著色器
    private lateinit var gradient: LinearGradient   // 線性漸層著色器

    init {
        initAttrs(context, attrs)
        val density = context.resources.displayMetrics.density
        processDegree = 360 * privateNumerator / privateDenominator
        ringRadius = DEFAULT_RADIUS_DP * density
        ringStokeWidth = DEFAULT_RING_STOKE_WIDTH_DP * density
        ringBackgroundPadding = DEFAULT_RING_BACKGROUND_PADDING_DP * density
        shadowLayer = DEFAULT_SHADOW_LAYER_DP * density
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val centerX = width / 2f
        val centerY = height / 2f

        val density = context.resources.displayMetrics.density

        ringScope.apply {
            left = centerX - ringRadius
            top = centerY - ringRadius
            right = centerX + ringRadius
            bottom = centerY + ringRadius
        }

        // 設計給的漸層參數 x1="87.5" y1="10.5" x2="16.5002" y2="70.5002"
        gradient = LinearGradient(
            ringScope.left + ringScope.width() * 0.875f, //起點色X
            ringScope.top + ringScope.height() * 0.105f, //起點色Y
            ringScope.left + ringScope.width() * 0.165f, //終點色X
            ringScope.top + ringScope.height() * 0.705f, //終點色Y
            ringGradientTopColor, //起點色
            ringGradientBottomColor, //終點色
            Shader.TileMode.CLAMP //邊界的染色方式
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

        textBottom = ringScope.top + TEXT_MARGIN_TOP * density
        textNumeratorRight = ringScope.centerX() - NUMERATOR_TEXT_MARGIN_RIGHT * density
        textNumeratorBottomLine = ringScope.bottom - NUMERATOR_TEXT_MARGIN_BOTTOM * density
        textDenominatorSlashLeft = ringScope.centerX()
        textDenominatorLeft = textDenominatorSlashLeft + (8 + 1) * density //text size + spacing
        textDenominatorBottomLine = ringScope.bottom - NUMERATOR_TEXT_MARGIN_BOTTOM * density

        textNumeratorPaint.apply {
            color = numeratorTextColor
            textAlign = Paint.Align.RIGHT
            textSize = 28 * density
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        textDenominatorPaint.apply {
            color = denominatorTextColor
            textAlign = Paint.Align.LEFT
            textSize = 16 * context.resources.displayMetrics.density
        }

        textPaint.apply {
            color = denominatorTextColor
            textAlign = Paint.Align.CENTER
            textSize = 12 * context.resources.displayMetrics.density
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
        canvas.drawPath(ringProgressPath, ringPaint)

        // 畫字
        canvas.drawText(
            privateNumerator.toString(),
            textNumeratorRight,
            textNumeratorBottomLine,
            textNumeratorPaint
        )
        canvas.drawText(
            "/",
            textDenominatorSlashLeft,
            textDenominatorBottomLine,
            textDenominatorPaint
        )
        canvas.drawText(
            "$privateDenominator",
            textDenominatorLeft, //text size + spacing
            textDenominatorBottomLine,
            textDenominatorPaint
        )
        canvas.drawText(
            text,
            ringScope.centerX(),
            textBottom,
            textPaint
        )
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.SafeCheckRingView)

        privateDenominator = a.getInt(R.styleable.SafeCheckRingView_denominator, 8)
        privateNumerator = a.getInt(R.styleable.SafeCheckRingView_numerator, 0)
        text = a.getString(R.styleable.SafeCheckRingView_text) ?: ""
        denominatorTextColor =
            a.getColor(R.styleable.SafeCheckRingView_denominatorTextColor, Color.BLACK)
        numeratorTextColor =
            a.getColor(R.styleable.SafeCheckRingView_numeratorTextColor, Color.GREEN)
        shadowColor = a.getColor(R.styleable.SafeCheckRingView_shadowColor, Color.GRAY)
        ringBackColor = a.getColor(R.styleable.SafeCheckRingView_ringBackColor, Color.GRAY)
        ringGradientTopColor =
            a.getColor(R.styleable.SafeCheckRingView_ringGradientTopColor, Color.GREEN)
        ringGradientBottomColor =
            a.getColor(R.styleable.SafeCheckRingView_ringGradientBottomColor, Color.BLUE)

        a.recycle()
    }

    private fun resetRingPath() {
        if (ringScope.isEmpty) return
        ringProgressPath.reset()
        ringProgressPath.addArc(
            ringScope,
            START_DEGREE,
            processDegree.toFloat()
        )
    }

    /**
     * 分母不得為0
     * 分子小於等於分母
     */
    fun setProgress(
        numerator: Int,
        denominator: Int,
    ) {
        valueAnimator?.cancel()
        privateDenominator = denominator
        privateNumerator = numerator
        processDegree = 360 * privateNumerator / privateDenominator
        invalidate()
    }

    /**
     * 分母不得為0
     * 分子小於等於分母
     */
    fun setProgressByAnimate(
        numerator: Int,
        denominator: Int,
        animateEndCallbacks: (() -> Unit)? = null
    ) {
        privateDenominator = denominator
        privateNumerator = numerator

        val fixedStartProgress = 0
        val fixedEndProgress = 360 * privateNumerator / privateDenominator

        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(fixedStartProgress, fixedEndProgress).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = abs(fixedEndProgress - fixedStartProgress) * DEFAULT_ANIMATION_DURATION
            addUpdateListener {
                processDegree = it.animatedValue as Int
                invalidate()
                if (processDegree == fixedEndProgress) { // animate ended
                    it.pause()
                    animateEndCallbacks?.invoke()
                }
            }
            start()
        }
    }
}