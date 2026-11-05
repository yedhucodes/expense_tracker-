package com.expensetracker.app.core.design.animation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Clean, lifecycle-aware Lottie illustration wrapper.
 * Plays locally packaged Bodymovin animations with loop/iteration control.
 */
@Composable
fun LottieIllustration(
    @RawRes rawRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    iterations: Int = LottieConstants.IterateForever,
    isPlaying: Boolean = true,
    speed: Float = 1.0f
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = iterations,
        speed = speed
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}

