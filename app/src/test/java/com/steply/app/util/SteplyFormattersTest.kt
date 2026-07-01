package com.steply.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SteplyFormattersTest {
    @Test
    fun `confidence percent is clamped and formatted`() {
        assertEquals("85%", formatConfidencePercent(0.85f))
        assertEquals("100%", formatConfidencePercent(1.2f))
        assertEquals("0%", formatConfidencePercent(-0.5f))
    }

    @Test
    fun `duration and chair stand display helpers format MVP labels`() {
        assertEquals("45 sec", formatDurationShort(45))
        assertEquals("1 min", formatDurationShort(60))
        assertEquals("12", formatChairStandCount(12))
        assertEquals("chair stands", formatChairStandUnitLabel())
        assertEquals("30-second chair stand", formatChairStandDurationLabel(30))
    }
}
