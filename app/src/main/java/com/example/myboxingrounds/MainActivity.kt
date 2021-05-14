package com.example.myboxingrounds

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var rounds: Int = 5
        var seconds: Int = 300
        var restTime: Int = 60
        var timerInterval: Int = 30
        var restInterval: Int = 15


        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
            }
        }

        // Set timer and rounds
        seekRounds.max = 30
        seekRounds.min = 1
        seekRounds.progress = 5
        seekRest.max = 20
        seekRest.min = 0
        seekRest.progress = 2
        seekTimer.max = 20
        seekTimer.min = 1
        seekTimer.progress = 4

        textRounds.text = seekRounds.progress.toString()
        textRest.text = convertToTime(seekRest.progress * restInterval)
        textTimer.text = convertToTime(seekTimer.progress * timerInterval)

        // Seekbar for rounds
        seekRounds.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                rounds = progress
                textRounds.text = rounds.toString()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        // Seekbar for rest
        seekRest.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                restTime = progress * restInterval
                textRest.text = convertToTime(restTime)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        // Seekbar for timer
        seekTimer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                seconds = progress * timerInterval
                textTimer.text = convertToTime(seconds)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        // Start button to start the timer
        buttonStart.setOnClickListener{

            val intent = Intent(this, ActiveTimerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP;
            intent.putExtra("rounds", rounds)
            intent.putExtra("seconds", seconds)
            intent.putExtra("rest", restTime)
            startActivity(intent)

        }

        val appSettingPrefs: SharedPreferences = getSharedPreferences( "AppSettingPrefs", 0)
        val sharedPrefsEditor: SharedPreferences.Editor = appSettingPrefs.edit()
        val isDark: Boolean = appSettingPrefs.getBoolean("darkmode", false)

        if( isDark ) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // dark/light mode button
        imageButton.setOnClickListener(View.OnClickListener {
            if( isDark ){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEditor.putBoolean("darkmode", false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEditor.putBoolean("darkmode", true)
            }
            sharedPrefsEditor.apply()
        })

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

    // function to convert progress to time string
    private fun convertToTime(timerProgress: Int): String {

        val hours = TimeUnit.MINUTES.toHours(timerProgress.toLong())
        val remainMinutes = timerProgress - TimeUnit.HOURS.toMinutes(hours)
        return String.format("%02d:%02d", hours, remainMinutes)

    }

}