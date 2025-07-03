package com.pichurchyk.budgetsaver.ui.theme // Make sure to replace com.yourpackage with your actual package name

import androidx.compose.ui.graphics.Color

// Colors derived from your app design screenshot, mapped to Material 3 Light Theme roles

// Primary Colors from Image (Green)
val primaryLight = Color(0xFF009963)         // Main green from your buttons and charts
val onPrimaryLight = Color(0xFFFFFFFF)        // White text observed on your green buttons
val primaryContainerLight = Color(0xFFE6FFEF) // A very light green tint, (previously identified as LightGreenBackground)
val onPrimaryContainerLight = Color(0xFF075E54) // Dark green text (e.g., "Welcome to Money Tracker") for contrast on primaryContainerLight

// Secondary Colors from Image (Blue accent from chart)
val secondaryLight = Color(0xFF34B7F1)       // Blue accent color from your chart
val onSecondaryLight = Color(0xFFFFFFFF)      // Assuming white text for contrast on the blue accent
val secondaryContainerLight = Color(0xFFE1F5FE) // A very light blue tint (standard practice, as not explicitly in image)
val onSecondaryContainerLight = Color(0xFF181D18) // Dark text (onBackgroundLight) for contrast on the light blue container

// Tertiary Colors from Image (Using Google Blue as an example, if applicable, or another accent)
// If Google Blue isn't meant to be tertiary, this could be another accent or primaryLight if no other distinct tertiary color.
val tertiaryLight = Color(0xFF4285F4)         // Google Blue from login button text (treated as tertiary for this example)
val onTertiaryLight = Color(0xFFFFFFFF)       // White text for contrast on Google Blue
val tertiaryContainerLight = Color(0xFFE3F2FD) // A very light tint of Google Blue (standard practice)
val onTertiaryContainerLight = Color(0xFF181D18) // Dark text (onBackgroundLight) for contrast

// Error Colors (Using your provided standard values as they are semantic)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)

// Background & Surface Colors from Image
val backgroundLight = Color(0xFFF7FCFA)       // Main background of your app screens (observed as white)
val onBackgroundLight = Color(0xFF1C1C1E)     // Darkest text for primary content on background (e.g., "Spending over time")

val surfaceLight = Color(0xFFFFFFFF)          // Card backgrounds, etc. (observed as white)
val onSurfaceLight = Color(0xFF1C1C1E)        // Darkest text for content on surfaces

// Surface Variants & Outlines from Image
val surfaceVariantLight = Color(0xFFF7F7F7)   // Bottom navigation, search bar background (observed light gray/off-white)
val onSurfaceVariantLight = Color(0xFF075E54) // Dark green text/icon (e.g., "Settings" on bottom nav) provides good contrast here
// Alternative: Color(0xFF414942) from your example if preferred, or onBackgroundLight (0xFF181D18)

val outlineLight = Color(0xFFE0E0E0)          // Subtle dividers observed or assumed in your design
val outlineVariantLight = Color(0xFFB0B0B0)   // More prominent outlines, using the placeholder gray from your design

// Scrim & Inverse Colors
val scrimLight = Color(0xFF000000)            // Standard scrim color

val inverseSurfaceLight = Color(0xFF2C322D)   // Using your example dark inverse surface color
// Alternative from image: Color(0xFF1C1C1E) (onBackgroundLight)
val inverseOnSurfaceLight = Color(0xFFEDF2EB) // Using your example light text on dark inverse surface
// Alternative from image: Color(0xFFFFFFFF) (onPrimaryLight)
val inversePrimaryLight = Color(0xFF96D5A7)   // Using your example, a lighter, accessible primary for dark surfaces
// Alternative from image if this specific shade isn't available: primaryLight (0xFF25D366) might work depending on the inverseSurface

// Extended Surface Container Tones (Mapped from image grays and white)
val surfaceDimLight = Color(0xFFD6DBD4)       // Using your example, a darker dim surface
// Alternative from image: Color(0xFFF0F0F0) (subtle background gray)
val surfaceBrightLight = Color(0xFFF6FBF3)    // Using your example, a bright surface (close to image's white)
// Alternative from image: Color(0xFFFFFFFF) (backgroundLight)

val surfaceContainerLowestLight = Color(0xFFFFFFFF) // Purest white from your design
val surfaceContainerLowLight = Color(0xFFF7F7F7)    // Lightest gray from your design (e.g., bottom nav)
val surfaceContainerLight = Color(0xFFF0F0F0)     // A general light gray container background from your design
val surfaceContainerHighLight = Color(0xFFEAEFE8)  // Using your example, a step darker
// Alternative from image: Color(0xFFE0E0E0) (divider gray)
val surfaceContainerHighestLight = Color(0xFFDFE4DD) // Using your example, the most emphasized neutral container
// Alternative from image: Color(0xFFB0B0B0) (placeholder gray)

// Other specific colors from your design, if they don't fit M3 roles directly
// but you want to keep them:
val imagePlaceholderGray = Color(0xFFB0B0B0) // Gray for "Add Image" icon, input placeholders
val mediumGrayTextLight = Color(0xFF8A8A8E) // Lighter gray text (e.g., "Last 30 days") - can be mapped to onSurfaceVariant or used directly
val disableGrey = Color(0xFF8A8A8E) // Lighter gray text (e.g., "Last 30 days") - can be mapped to onSurfaceVariant or used directly

val green = Color(0xFF009963)
val red = Color(0xFFBA1A1A)

val notificationGreenLight = Color(0xffd6e8e1)
val notificationRedLight = Color(0xfff1cbcb)
val notificationYellowLight = Color(0xffedd770)

val notificationGreenDark = Color(0xFF009963)
val notificationRedDark = Color(0xFFBA1A1A)
val notificationYellowDark = Color(0xff876e00)
