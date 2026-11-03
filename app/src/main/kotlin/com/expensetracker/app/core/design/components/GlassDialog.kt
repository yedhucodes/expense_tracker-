package com.expensetracker.app.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.expensetracker.app.core.design.theme.AppSpacing
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.design.theme.PillShape

/**
 * Liquid Glass Modal Dialog with specular highlight gloss line,
 * dark frosted gradient surface, and metallic reflection borders.
 */
@Composable
fun GlassDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = AppTheme.colors.isDark
    val dialogShape = RoundedCornerShape(28.dp)
    val backgroundBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(Color(0xF0182232), Color(0xF50F1622))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFAFFFFFF), Color(0xF0F1F5F9))
        )
    }

    val borderBrush = Brush.verticalGradient(
        listOf(
            if (isDark) Color(0x70FFFFFF) else Color(0x99FFFFFF),
            if (isDark) Color(0x15FFFFFF) else Color(0x1A000000)
        )
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .clip(dialogShape)
                .background(backgroundBrush, dialogShape)
                .border(1.dp, borderBrush, dialogShape)
        ) {
            // Top Specular Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                if (isDark) Color(0x60FFFFFF) else Color(0x90FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = AppTheme.typography.titleLarge,
                        color = AppTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                }

                content()

                if (confirmButton != null || dismissButton != null) {
                    Spacer(modifier = Modifier.height(AppSpacing.lg))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dismissButton != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                dismissButton()
                            }
                        }

                        if (dismissButton != null && confirmButton != null) {
                            Spacer(modifier = Modifier.width(AppSpacing.md))
                        }

                        if (confirmButton != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }
    }
}
