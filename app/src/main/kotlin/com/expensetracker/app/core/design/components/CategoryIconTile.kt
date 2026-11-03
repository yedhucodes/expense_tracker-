package com.expensetracker.app.core.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.expensetracker.app.core.design.category.CategoryStyle
import com.expensetracker.app.core.design.icons.IconRegistry
import com.expensetracker.app.core.design.theme.CategoryTileShape

enum class TileSize(val containerSize: Dp, val iconSize: Dp) {
    Small(36.dp, 18.dp),
    Standard(48.dp, 24.dp),
    Large(56.dp, 28.dp)
}

@Composable
fun CategoryIconTile(
    category: CategoryStyle,
    modifier: Modifier = Modifier,
    size: TileSize = TileSize.Standard,
    contentDescriptionText: String = category.name
) {
    CategoryIconTile(
        iconKey = category.iconKey,
        color = category.color,
        modifier = modifier,
        size = size,
        contentDescriptionText = contentDescriptionText
    )
}

@Composable
fun CategoryIconTile(
    iconKey: String,
    color: Color,
    modifier: Modifier = Modifier,
    size: TileSize = TileSize.Standard,
    contentDescriptionText: String = "Category icon"
) {
    val backgroundColor = color.copy(alpha = 0.16f)

    Box(
        modifier = modifier
            .size(size.containerSize)
            .background(color = backgroundColor, shape = CategoryTileShape)
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.35f),
                shape = CategoryTileShape
            )
            .semantics { contentDescription = contentDescriptionText },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = IconRegistry.getIcon(iconKey),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(size.iconSize)
        )
    }
}

