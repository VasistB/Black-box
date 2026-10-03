package com.vasist.blackbox.domain.model

enum class ChallengeType {
    UPSIDE_DOWN,     // Flip phone upside down for 3 seconds
    CHARGER_PLUG,    // Connect charger
    SHOUT_WHISPER,   // Whisper then shout into mic
    SCREEN_OFF_HOLD, // Screen off / Power button hold for 5 seconds
    VOLUME_EXTREMES, // Mute to 0% then crank to 100% volume
    GPS_WANDERER,    // Move 10 meters in real world
    SHAKE_IT          // Shake device vigorously
}
