package com.example.myboxingrounds

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_active_timer.*
import java.util.concurrent.TimeUnit


class ActiveTimerActivity : AppCompatActivity() {

    enum class TimerState{ Stopped, Paused, Running }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds:Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining:Long = 0
    private var secondsPerRound:Int = 0
    private var rounds:Int = 0
    private var roundsRemaining:Int = 0
    private var resting:Boolean = false
    private var restPerRound:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_timer)

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        rounds = intent.getIntExtra("rounds", 0)
        secondsPerRound = intent.getIntExtra("seconds", 0)
        restPerRound = intent.getIntExtra("rest", 0)
        roundsRemaining = rounds
        timerLengthSeconds = secondsPerRound.toLong()

        startTimer()

        buttonPause.setOnClickListener {

            if( timerState == TimerState.Running ){
                pauseTimer()
            } else if ( timerState == TimerState.Paused ) {
                resumeTimer()
            }

        }

        buttonReset.setOnClickListener {

            if( timerState == TimerState.Paused ){
                timer.cancel()
                finish()
            }

        }

    }


    // starts timer with using timerLengthSeconds
    private fun startTimer(){
        timerState = TimerState.Running
        textActiveTimer.text = convertToTime(timerLengthSeconds.toInt())
        timerLengthSeconds++ // For smoother UX, add 1 extra second so the user sees the set time every time at the beginning
        timer = object : CountDownTimer((timerLengthSeconds * 1000), 1000){

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                textActiveTimer.text = convertToTime(secondsRemaining.toInt())

                // if tick is equal to 10 seconds, play 10 second warning sound
            }

            override fun onFinish() {
                onTimerFinished()
            }
        }
        updateUI()
        timer.start()
    }

    private fun pauseTimer(){
        timerState = TimerState.Paused
        updateUI()
        timer.cancel()
    }

    private fun resumeTimer(){
        timerLengthSeconds = secondsRemaining
        startTimer()
    }

    private fun onTimerFinished(){

        timer.cancel()
        timerState = TimerState.Stopped

        if ( roundsRemaining >= 2 ) {

            if ( resting ) {
                timerLengthSeconds = secondsPerRound.toLong()
                resting = false
                roundsRemaining--
            } else {
                timerLengthSeconds = restPerRound.toLong()
                resting = true
            }

            startTimer()
            timer.start()
            updateUI()
        } else {
            finish()
        }

    }

    private fun updateUI(){

        if( timerState == TimerState.Running ){

            if(resting && restPerRound > 0) textActiveRounds.text = "RESTING" else textActiveRounds.text = "$roundsRemaining/$rounds"
            buttonPause.text = "Pause"
            buttonReset.setTextColor(Color.parseColor("#808080"));

            // Change button color to green
        } else if ( timerState == TimerState.Paused ) {

            buttonPause.text = "Resume"
            buttonReset.setTextColor(Color.parseColor("#000000"));
            textActiveRounds.text = "PAUSED"
            // change button color to red

        } else if (timerState == TimerState.Stopped) {

            textActiveRounds.text = "$roundsRemaining/$rounds"

        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    // function to convert progress to time
    private fun convertToTime(timerProgress: Int): String {

        val hours = TimeUnit.MINUTES.toHours(timerProgress.toLong())
        val remainMinutes = timerProgress - TimeUnit.HOURS.toMinutes(hours)
        return String.format("%02d:%02d", hours, remainMinutes)

    }

}