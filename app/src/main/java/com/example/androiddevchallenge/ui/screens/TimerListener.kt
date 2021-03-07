package com.example.androiddevchallenge.ui.screens

interface TimerListener {
    fun onStart()
    fun onPause()
    fun onStop()

    fun onTick(ms:Long)
}