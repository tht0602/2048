package tzu.hsuan.tseng.tooowlforate

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase


class TestActivity : Activity() {

    private var cardNumber = 0
    private var userId = 0
    private lateinit var ani: AnimatorSet
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        findViewById<FlipNumberView>(R.id.fnv_one).apply {
            setCardNumberWithoutAnimate(5)
            setOnClickListener {
                it as FlipNumberView
                it.startAnimate((it.cardNumber + 1) % 10)
            }
        }
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.setUserProperty("ServiceName", "JFT")
        findViewById<View>(R.id.view_test).setOnClickListener {
            firebaseAnalytics.resetAnalyticsData()
            firebaseAnalytics.setUserId((userId++).toString())
            firebaseAnalytics.logEvent("JUST_FOR_TEST3") {
                param("i.am.the.test.param", "right")
            }
            animateWay4()
        }
    }


    private fun animateWay1() {
        val ani1 = (AnimatorInflater.loadAnimator(this, R.animator.test_animator) as AnimatorSet).apply {
            setTarget(findViewById(R.id.view_test))
        }
        ani1.start()
        if (ani.isRunning) {
            // 可以判斷動畫有沒有在跑
        }
    }

    private fun animateWay2() {
        val viewTest = findViewById<View>(R.id.view_test)
        viewTest.translationX = 0f
        viewTest.animate().translationX(500f).setDuration(1000).setInterpolator(LinearInterpolator()).withEndAction {
            //播放完的callback
        }.start()
    }

    private fun animateWay3() {
        val viewTest = findViewById<View>(R.id.view_test2)
        viewTest.pivotY = 0f
        viewTest.animate().translationX(500f).setDuration(1000).withEndAction {
            //播放完的callback
        }.start()
    }

    private fun animateWay4() {
        val viewGreen = findViewById<View>(R.id.view_test3)
        val viewPurple = findViewById<View>(R.id.view_test4)

        viewGreen.cameraDistance = 3600f
        viewPurple.cameraDistance = 3600f

        viewGreen.pivotY = viewGreen.measuredHeight.toFloat()
        viewPurple.pivotY = 0f

        viewGreen.alpha = 1f
        viewPurple.rotationX = 90f
        viewPurple.alpha = 0.5f

        val ani1 = (AnimatorInflater.loadAnimator(this, R.animator.flip) as AnimatorSet).apply {
            setTarget(viewGreen)
        }
        val ani2 =
            (AnimatorInflater.loadAnimator(this, R.animator.bot_flip) as AnimatorSet).apply {
                setTarget(viewPurple)
            }
        ani = AnimatorSet().apply {
            playTogether(ani1, ani2)
        }
        ani.start()
    }

}