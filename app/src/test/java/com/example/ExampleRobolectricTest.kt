package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.InitialMarketData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleRobolectricTest {

    @Test
    fun appContext_hasDhanRatanGamesName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DhanRatan Games", appName)
    }

    @Test
    fun initialMarkets_hasMainAndStarlineActive() {
        val mainMarkets = InitialMarketData.INITIAL_MARKETS.filter { it.category == "main" }
        val starlineMarkets = InitialMarketData.INITIAL_MARKETS.filter { it.category == "starline" }
        assertEquals(16, mainMarkets.size)
        assertEquals(12, starlineMarkets.size)
        assertTrue(InitialMarketData.INITIAL_MARKETS.all { it.isLive })
        assertEquals("6", InitialMarketData.calculateAnkFromPana("123"))
        assertTrue(InitialMarketData.DEFAULT_WELCOME_NOTICE_TITLE.contains("DhanRatan Games"))
    }

    @Test
    fun doublePattiValidation_requiresExactlyTwoRepeatingDigits() {
        assertTrue(InitialMarketData.isValidDoublePatti("220"))
        assertTrue(InitialMarketData.isValidDoublePatti("118"))
        assertTrue(InitialMarketData.isValidDoublePatti("599"))
        assertFalse(InitialMarketData.isValidDoublePatti("128"))
        assertFalse(InitialMarketData.isValidDoublePatti("777"))
    }
}

