package com.example.a7minutesworkoutapp

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkoutapp.databinding.ActivityExercisesBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class ExercisesActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExercisesBinding? = null

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exerciesTimer: CountDownTimer? = null
    private var exerciesProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts : TextToSpeech? = null
    private var player:MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExercisesBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        tts = TextToSpeech(this,this)

        setSupportActionBar(binding?.ExercisesToolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

        }

        exerciseList = Constants.defaultExercieseList()

        binding?.ExercisesToolbar?.setNavigationOnClickListener {
            onBackPressed()

        }

        setupRestView()

    }

    private fun setupRestView() {

        try {
            val soundURI = Uri.parse(
                "android.resource://com.example.a7minutesworkoutapp/" + R.raw.app_src_main_res_raw_press_start)
            player = MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping = false
            player?.start()
        }catch (e: Exception){
            e.printStackTrace()
        }

        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciesView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility = View.VISIBLE
        binding?.tvNextExercise?.visibility = View.VISIBLE


        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.tvNextExercise?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()

    }

    private fun setupExerciesView() {
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciesView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvUpcomingLabel?.visibility = View.INVISIBLE
        binding?.tvNextExercise?.visibility = View.INVISIBLE
        if (exerciesTimer != null) {
            exerciesTimer?.cancel()
            exerciesProgress = 0

        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciesProgressBar()
    }

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                setupExerciesView()


            }

        }.start()

    }

    private fun setExerciesProgressBar() {
        binding?.progressBarExercies?.progress = exerciesProgress

        exerciesTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(p0: Long) {
                exerciesProgress++
                binding?.progressBarExercies?.progress = 30 - exerciesProgress
                binding?.tvTimerExercies?.text = (30 - exerciesProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    setupRestView()
                } else {
                    Toast.makeText(
                        this@ExercisesActivity,
                        "Congr.! you ar completed the 7 minuts workout",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (exerciesTimer != null) {
            exerciesTimer?.cancel()
            exerciesProgress = 0

        }

        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player != null){
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language is not Suported")
            }

        } else {
            Log.e("TTS", "Initilzation Failed")
        }
    }

    private fun speakOut(text:String){

        tts?.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }




}