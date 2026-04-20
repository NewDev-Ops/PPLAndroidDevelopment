package com.pocketpal.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val PocketPalShapes = Shapes(
    // Extra small - chips, small buttons
    extraSmall = RoundedCornerShape(8.dp),
    
    // Small - small buttons, input fields
    small = RoundedCornerShape(12.dp),
    
    // Medium - cards, dialogs (our default)
    medium = RoundedCornerShape(16.dp),
    
    // Large - bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    
    // Extra large - modal sheets, full-screen dialogs
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom corner radii for specific use cases
object PocketPalCornerRadii {
    val Small = 12.dp
    val Medium = 16.dp
    val Large = 24.dp
    val ExtraLarge = 32.dp
    val Full = 9999.dp // For completely rounded pills
}