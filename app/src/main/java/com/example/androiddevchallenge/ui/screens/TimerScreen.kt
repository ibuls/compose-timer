package com.example.androiddevchallenge.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit

val DURATION = 60 * 1000L // duration in SEC
var timer: Job? = null

@ExperimentalAnimationApi
@Preview
@Composable
fun TimerScreen(

) {
    var mills = remember { mutableStateOf(DURATION) }
    var state = remember { mutableStateOf(TimerState.STOPPED) }



    DrawTimerScreen(mills.value, state.value, object : TimerListener {
        override fun onStart() {
            state.value = TimerState.RUNNING
        }


        override fun onPause() {
            state.value = TimerState.PAUSED
        }

        override fun onStop() {
            state.value = TimerState.STOPPED
            mills.value = DURATION

        }

        override fun onTick(ms: Long) {
            if (ms > 0) {
                mills.value = ms
            } else {
                state.value = TimerState.STOPPED
                mills.value = DURATION

            }
        }

    })
}

@ExperimentalAnimationApi
@Composable
fun DrawTimerScreen(
    mills: Long,
    state: TimerState,
    listener: TimerListener
) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        val (tvTime, timerCircle, btnPlay, btnStop, bgImg) = createRefs()

        //val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())

        Image(
            painter = painterResource(id = R.drawable.bg_pattern),
            contentDescription = "",
                contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs(bgImg) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        bottomMargin = 10.dp,
                        bias = 0.1f
                    )
                }
                .fillMaxSize()
                .alpha(0.1f)
        )

        DrawCircle(modifier = Modifier.constrainAs(timerCircle) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)

            linkTo(
                top = bgImg.top,
                bottom = bgImg.bottom,
                topMargin = 10.dp,
                bottomMargin = 10.dp,
                bias = 0.4f
            )
        }, Color.Black, mills)

        Text(
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mills),
                TimeUnit.MILLISECONDS.toSeconds(mills) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mills))
            ),
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier.constrainAs(tvTime) {
                top.linkTo(timerCircle.top)
                bottom.linkTo(timerCircle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

        DrawPlayBtn(Modifier.constrainAs(btnPlay) {
            top.linkTo(timerCircle.bottom, margin = 200.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, listener, state, mills)

        AnimatedVisibility(visible = state == TimerState.RUNNING,Modifier.constrainAs(btnStop) {
            top.linkTo(btnPlay.top)
            bottom.linkTo(btnPlay.bottom)
            start.linkTo(btnPlay.end, margin = 15.dp)
        } ) {
            DrawStopBtn(modifier = Modifier, listener, state, mills)
        }

    }
}

@Composable
fun DrawCircle(modifier: Modifier, color: Color, mills: Long) {
    var per = 0f
    if (mills > 0 && DURATION > 0) {
        per = (DURATION - mills).toFloat() / DURATION.toFloat()
    }
    val sweepAngle = animateFloatAsState(
        targetValue = per * 360,
        animationSpec = SpringSpec(Spring.DampingRatioNoBouncy, 5f, visibilityThreshold = 1 / 1000f)
    ).value
    Canvas(modifier = modifier
        .height(200.dp)
        .width(200.dp), onDraw = {
        val middle =
            Offset(size.minDimension / 2, size.minDimension / 2)
        drawCircle(
            color = color,
            center = middle,
            radius = size.minDimension / 2,
            style = Stroke(2.dp.toPx()),
        )




        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Green, Color.Red)),
            startAngle = 270f,
            sweepAngle = sweepAngle.toFloat(),
            useCenter = true,
            style = Stroke()
        )
        drawArc(
            brush = Brush.sweepGradient(listOf(Color.Green, Color.Green)),
            startAngle = 270f,
            sweepAngle = sweepAngle.toFloat(),
            useCenter = true
        )

    })
}

@Composable
fun DrawPlayBtn(
    modifier: Modifier, listener: TimerListener, state: TimerState, mills: Long

) {
    when (state) {
        TimerState.PAUSED, TimerState.STOPPED
        -> {
            Image(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "",
                modifier = modifier
                    .height(60.dp)
                    .width(60.dp)
                    .clickable {
                        listener.onStart()
                    }
            )
        }
        TimerState.RUNNING -> {
            Image(
                painter = painterResource(id = R.drawable.ic_pause),
                contentDescription = "",
                modifier = modifier
                    .height(60.dp)
                    .width(60.dp)
                    .clickable {
                        listener.onPause()
                    }
            )
            UpdateTime(mills, listener)
        }
    }


}


@ExperimentalAnimationApi
@Composable
fun DrawStopBtn(
    modifier: Modifier, listener: TimerListener, state: TimerState, mills: Long

) {
    when(state){
      TimerState.RUNNING ->  Image(
            painter = painterResource(id = R.drawable.ic_stop),
            contentDescription = "",
            modifier = modifier
                .height(30.dp)
                .width(30.dp)
                .clickable {
                    listener.onStop()
                }
        )
    }



}


@Composable
fun UpdateTime(mills: Long, listener: TimerListener) {
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        delay(1000)
        val newMs = mills - 1000
        listener.onTick(newMs)
    }
}


