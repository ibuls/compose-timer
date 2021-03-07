package com.example.androiddevchallenge

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive



fun CoroutineScope.launchPeriodicAsync(
    repeatMillis: Long,
    duration:Long,
    action: (Long) -> Unit
) = this.async {

    var mills = duration
    if (mills > 0) {
        while (isActive) {
            action(mills)
            mills -= repeatMillis
            delay(repeatMillis)
        }
    }
}