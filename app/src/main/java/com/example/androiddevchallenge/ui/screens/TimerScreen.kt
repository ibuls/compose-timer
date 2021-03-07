package com.example.androiddevchallenge.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.R
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.androiddevchallenge.ui.screens.TimerState.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val DURATION  = 60 *1000L // duration in SEC
var timer:Job? = null
@Preview
@Composable
fun TimerScreen() {
    var mills =  remember { mutableStateOf(DURATION)}
    var state = remember { mutableStateOf(STOPPED)}



    DrawTimerScreen(mills.value ,state.value,object :TimerListener{
        override fun onStart() {
            state.value = RUNNING
        }


        override fun onPause() {
            state.value = PAUSED
        }

        override fun onStop() {
            state.value = STOPPED
        }

        override fun onTick(ms: Long) {
            if (ms>0){
                mills.value = ms
            }else{
                state.value = STOPPED
                mills.value = DURATION

            }
        }

    })
}

@Composable
fun DrawTimerScreen(
    mills: Long,
    state: TimerState,
    listener: TimerListener
) {

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (tvTime,timerCircle,btnPlay) = createRefs()

        //val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        DrawCircle(modifier = Modifier.constrainAs(timerCircle){
            start.linkTo(parent.start)
            end.linkTo(parent.end)

            linkTo(
                top = parent.top,
                bottom = parent.bottom,
                topMargin = 10.dp,
                bottomMargin = 10.dp,
                bias = 0.4f
            )
        },MaterialTheme.colors.primary,mills)

        Text(
            String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mills),
                TimeUnit.MILLISECONDS.toSeconds(mills) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mills))
            ),
            fontSize = 32.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.constrainAs(tvTime) {
                top.linkTo(timerCircle.top)
                bottom.linkTo(timerCircle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

        DrawButtons(Modifier.constrainAs(btnPlay){
            top.linkTo(timerCircle.bottom,margin = 100.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        },listener,state,mills)



    }
}

@Composable
fun DrawCircle(modifier: Modifier, color: Color,mills: Long){


    Canvas(modifier = modifier
        .height(200.dp)
        .width(200.dp), onDraw = {
        val middle =
            Offset(size.minDimension / 2, size.minDimension / 2)
        drawCircle(
            color = color,
            center = middle,
            radius = size.minDimension/2,
            style = Stroke(8.dp.toPx()),
        )

        var per = 0f
        if(mills>0 && DURATION>0){
            per = (DURATION-mills).toFloat()/ DURATION.toFloat()
        }
        val sweepAngle = per*360

        drawArc(brush = Brush.sweepGradient(listOf(Color.Red,Color.Cyan)),startAngle = 270f,sweepAngle = sweepAngle.toFloat(),useCenter = true )

    })
}
@Composable
fun DrawButtons(
    modifier: Modifier, listener: TimerListener, state: TimerState, mills: Long

){
    when(state){
        PAUSED,STOPPED
         ->{
            Image(
                painter = painterResource(id =R.drawable.ic_play),
                contentDescription = "",
                modifier = modifier
                    .height(60.dp)
                    .width(60.dp)
                    .clickable {
                        listener.onStart()
                    }
            )
        }
        RUNNING -> {
            Image(
                painter = painterResource(id =R.drawable.ic_pause),
                contentDescription = "",
                modifier = modifier
                    .height(60.dp)
                    .width(60.dp)
                    .clickable {
                        listener.onPause()
                    }
            )
            UpdateTime(mills,listener)
        }
    }


}

@Composable
fun UpdateTime(mills: Long, listener: TimerListener) {
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        delay(1000)
        val newMs = mills-1000
        listener.onTick(newMs)
    }
}


