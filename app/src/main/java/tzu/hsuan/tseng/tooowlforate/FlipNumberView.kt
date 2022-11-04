package tzu.hsuan.tseng.tooowlforate

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import tzu.hsuan.tseng.tooowlforate.databinding.FlipNumberViewBinding

/**
 * @author Tom on 2022/7/26
 * 行員321翻牌動畫
 */
class FlipNumberView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: FlipNumberViewBinding
    private lateinit var ani: AnimatorSet
    var cardNumber = 0

    companion object {
        private const val DEFAULT_CAMERA_DISTANCE = 1280f //依裝置不同cameraDistance預設值可能會有所變化
        private val UPPER_CARD_IMG = listOf(
            R.drawable.upper_half_0, R.drawable.upper_half_1,
            R.drawable.upper_half_2, R.drawable.upper_half_3,
            R.drawable.upper_half_4, R.drawable.upper_half_5,
            R.drawable.upper_half_6, R.drawable.upper_half_7,
            R.drawable.upper_half_8, R.drawable.upper_half_9
        )
        private val LOWER_CARD_IMG = listOf(
            R.drawable.lower_half_0, R.drawable.lower_half_1,
            R.drawable.lower_half_2, R.drawable.lower_half_3,
            R.drawable.lower_half_4, R.drawable.lower_half_5,
            R.drawable.lower_half_6, R.drawable.lower_half_7,
            R.drawable.lower_half_8, R.drawable.lower_half_9
        )
    }

    fun setCardNumberWithoutAnimate(newCardNumber: Int){
        cardNumber = newCardNumber
        if (this::ani.isInitialized) {
            if (ani.isRunning) {ani.end()}
        }
        binding.apply {
            top.setImageDrawable(AppCompatResources.getDrawable(context, UPPER_CARD_IMG[cardNumber]))
            bot.setImageDrawable(AppCompatResources.getDrawable(context, LOWER_CARD_IMG[cardNumber]))
            topFlip.setImageDrawable(AppCompatResources.getDrawable(context, UPPER_CARD_IMG[cardNumber]))
            botFlip.setImageDrawable(AppCompatResources.getDrawable(context, LOWER_CARD_IMG[cardNumber]))
        }
    }

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        inflate(context, R.layout.flip_number_view, this)
        binding = FlipNumberViewBinding.bind(this)
        setCardCameraDistance(DEFAULT_CAMERA_DISTANCE)
    }

    /**
     * @param distance 改變牌面翻動時視覺上的距離感，數字越小牌面翻動的形變會越大，預設是1920
     */
    fun setCardCameraDistance(distance: Float) {
        binding.botFlip.cameraDistance = distance
        binding.topFlip.cameraDistance = distance
    }

    private fun initAnimate() {
        //請注意，在onCreate()時無法取得measuredHeight
        binding.topFlip.pivotY = binding.topFlip.measuredHeight.toFloat()
        binding.botFlip.pivotY = 0f

        val ani1 = (AnimatorInflater.loadAnimator(context, R.animator.flip) as AnimatorSet).apply {
            interpolator = LinearInterpolator()
            setTarget(binding.topFlip)
        }
        val ani2 =
            (AnimatorInflater.loadAnimator(context, R.animator.bot_flip) as AnimatorSet).apply {
                interpolator = LinearInterpolator()
                setTarget(binding.botFlip)
            }
        ani = AnimatorSet().apply {
            playTogether(ani1, ani2)
        }
    }

    fun startAnimate(nextCardNumber: Int) {
        if (!this::ani.isInitialized) {
            initAnimate()
        }
        if (!ani.isRunning) {
            binding.apply {
                top.setImageDrawable(AppCompatResources.getDrawable(context, UPPER_CARD_IMG[nextCardNumber]))
                bot.setImageDrawable(AppCompatResources.getDrawable(context, LOWER_CARD_IMG[cardNumber]))
                topFlip.setImageDrawable(AppCompatResources.getDrawable(context, UPPER_CARD_IMG[cardNumber]))
                botFlip.setImageDrawable(AppCompatResources.getDrawable(context, LOWER_CARD_IMG[nextCardNumber]))
            }
            ani.start()
            cardNumber = nextCardNumber
        }
    }
}