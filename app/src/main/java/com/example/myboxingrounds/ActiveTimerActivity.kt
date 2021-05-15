package com.example.myboxingrounds

import android.util.Log
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
    private var state:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_timer)

        if (savedInstanceState == null) {

            rounds = intent.getIntExtra("rounds", 0)
            secondsPerRound = intent.getIntExtra("seconds", 0)
            restPerRound = intent.getIntExtra("rest", 0)
            timerLengthSeconds = secondsPerRound.toLong()

        } else {

            timerState = savedInstanceState.getSerializable("timerState") as TimerState
            rounds = savedInstanceState.getInt("rounds", rounds)
            secondsPerRound = savedInstanceState.getInt("secondsPerRound")
            restPerRound = savedInstanceState.getInt("restPerRound")
            roundsRemaining = savedInstanceState.getInt("roundsRemaining")
            secondsRemaining = savedInstanceState.getLong("secondsRemaining")
            resting =  savedInstanceState.getBoolean("resting")
            timerLengthSeconds = secondsRemaining

        }

        if ( roundsRemaining == 0 ) roundsRemaining = rounds

        if ( timerState == TimerState.Paused ) {
            updateUI()
        } else if ( timerState == TimerState.Stopped || timerState == TimerState.Running ){
            startTimer()
        }

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

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

    }

    // disables navbar when app is in focus
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("rounds", rounds)
        savedInstanceState.putInt("secondsPerRound", secondsPerRound)
        savedInstanceState.putInt("restPerRound", restPerRound)
        savedInstanceState.putInt("roundsRemaining", roundsRemaining)
        savedInstanceState.putLong("secondsRemaining", secondsRemaining)
        savedInstanceState.putBoolean("resting", resting)
        savedInstanceState.putSerializable("timerState", timerState)

    }

    // timer with using timerLengthSeconds
    private fun startTimer(){
        timerState = TimerState.Running
        textActiveTimer.text = convertToTime(timerLengthSeconds.toInt())
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
            textActiveTimer.text = convertToTime(secondsRemaining.toInt())
            // change button color to red

        }

    }

    // Sets immersive, but leaves status bar on
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // function to convert progress to time string
    private fun convertToTime(timerProgress: Int): String {

        val hours = TimeUnit.MINUTES.toHours(timerProgress.toLong())
        val remainMinutes = timerProgress - TimeUnit.HOURS.toMinutes(hours)
        return String.format("%02d:%02d", hours, remainMinutes)

    }

}