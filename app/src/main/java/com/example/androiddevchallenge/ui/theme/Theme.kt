/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0X6200EE),
    primaryVariant = Color(0X3700B3),
    secondary = Color(0X03DAC6),
    secondaryVariant = Color(0X018786),
    background = Color(0XFFFFFF),
    surface = Color(0XFFFFFF),
    error = Color(0XB00020),
    onPrimary = Color(0XFFFFFF),
    onSecondary = Color(0X000000),
    onBackground = Color(0X000000),
    onSurface = Color(0X000000),
    onError = Color(0XFFFFFF),
)

private val LightColorPalette = lightColors(
    primary = Color(0XBB86FC),
    primaryVariant = Color(0X3700B3),
    secondary = Color(0X03DAC6),
    secondaryVariant = Color(0X03DAC6),
    background = Color(0X121212),
    surface = Color(0X121212),
    error = Color(0XCF6679),
    onPrimary = Color(0X000000),
    onSecondary = Color(0X000000),
    onBackground = Color(0XFFFFFF),
    onSurface = Color(0XFFFFFF),
    onError = Color(0X000000),
)

@Composable
fun MyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
