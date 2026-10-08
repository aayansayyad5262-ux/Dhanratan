package com.example.data.local

import com.example.data.model.ChartEntry
import com.example.data.model.GameRateItem
import com.example.data.model.GameVariety
import com.example.data.model.MarketItem
import com.example.data.model.StarlineDateSlot
import com.example.data.model.SupportMessageEntity

object InitialMarketData {

    const val MERCHANT_UPI_ID = "9021786641-7@ybl"
    const val MERCHANT_NAME = "DhanRatan Games"
    const val SUPPORT_WHATSAPP_NUMBER = "9021786641"
    const val SUPPORT_TELEGRAM_HANDLE = "I_0g_00"
    const val ADMIN_PERMANENT_PASSWORD = "DR@123456"

    const val ADMIN_LOGIN_USERNAME = "Admin"
    const val ADMIN_LOGIN_PHONE = "1234567890"
    const val ADMIN_LOGIN_PASSWORD = "1234567890"
    const val ADMIN_LOGIN_MPIN = "7860"

    fun isSpecialAdminUser(phone: String?): Boolean {
        return phone?.filter { it.isDigit() } == ADMIN_LOGIN_PHONE
    }

    fun verifyAdminPassword(input: String): Boolean {
        return input == ADMIN_PERMANENT_PASSWORD || input == ADMIN_LOGIN_PASSWORD
    }

    val ALL_12_MAIN_GAMES: List<GameVariety> = listOf(
        GameVariety.SINGLE_ANK,
        GameVariety.SINGLE_DIGIT_BULK,
        GameVariety.JODI,
        GameVariety.JODI_DIGIT_BULK,
        GameVariety.SINGLE_PATTI,
        GameVariety.SINGLE_PANA_BULK,
        GameVariety.DOUBLE_PATTI,
        GameVariety.DOUBLE_PANA_BULK,
        GameVariety.TRIPLE_PATTI,
        GameVariety.HALF_SANGAM_A,
        GameVariety.HALF_SANGAM_B,
        GameVariety.FULL_SANGAM
    )

    val STARLINE_GAMES: List<GameVariety> = listOf(
        GameVariety.SINGLE_ANK,
        GameVariety.SINGLE_DIGIT_BULK,
        GameVariety.SINGLE_PATTI,
        GameVariety.SINGLE_PANA_BULK,
        GameVariety.DOUBLE_PATTI,
        GameVariety.DOUBLE_PANA_BULK,
        GameVariety.TRIPLE_PATTI
    )

    const val DEFAULT_WELCOME_NOTICE_TITLE = "Welcome to DhanRatan Games Official APK"
    const val DEFAULT_WELCOME_NOTICE_BODY = "Play official Main Markets & DhanRatan Starline safely. Instant Payment Gateway deposits (₹100 - ₹10,000) & fast Bank/UPI withdrawals (10:00 AM - 09:00 PM)."

    // Main Markets as instructed:
    // Bid open Time = Open result bidding closes at this time (place Open bids before this time)
    // Bid close Time = Close result bidding closes at this time (place Close bids before this time)
    val INITIAL_MARKETS: List<MarketItem> = listOf(
        MarketItem(
            id = "main-01-karnataka-day",
            name = "KARNATAKA DAY",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "10:15 AM",
            closeTime = "11:15 AM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-02-sridevi",
            name = "SRIDEVI",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "11:40 AM",
            closeTime = "12:40 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-03-time-bazar",
            name = "TIME BAZAR",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "01:05 PM",
            closeTime = "02:05 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-04-madhur-day",
            name = "MADHUR DAY",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "01:35 PM",
            closeTime = "02:35 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-05-milan-day",
            name = "MILAN DAY",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "03:05 PM",
            closeTime = "05:05 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-06-rajdhani-day",
            name = "RAJDHANI DAY",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "03:10 PM",
            closeTime = "05:10 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-07-supreme-day",
            name = "SUPREME DAY",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "03:35 PM",
            closeTime = "05:35 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-08-kalyan",
            name = "KALYAN",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "04:15 PM",
            closeTime = "06:15 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-09-karanataka-night",
            name = "KARANATAKA NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "06:45 PM",
            closeTime = "07:45 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-10-sridevi-night",
            name = "SRIDEVI NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "07:20 PM",
            closeTime = "08:20 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-11-madhur-night",
            name = "MADHUR NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "08:30 PM",
            closeTime = "10:30 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-12-supreme-night",
            name = "SUPREME NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "08:35 PM",
            closeTime = "10:35 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-13-milan-night",
            name = "MILAN NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "09:05 PM",
            closeTime = "11:05 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-14-kalyan-night",
            name = "KALYAN NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "09:20 PM",
            closeTime = "11:20 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-15-rajdhani-night",
            name = "RAJDHANI NIGHT",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "09:35 PM",
            closeTime = "11:35 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        MarketItem(
            id = "main-16-main-bazar",
            name = "MAIN BAZAR",
            category = "main",
            resultCode = "***-**-***",
            status = "Betting is Running",
            openTime = "09:55 PM",
            closeTime = "11:55 PM",
            isLive = true,
            multiplierLabel = "",
            chartHistory = emptyList()
        ),
        // DHANRATAN STARLINE MARKETS: Market Time (10:15 AM - 09:15 PM) & Market Date
        MarketItem(
            id = "dhanratan-starline-1015am",
            name = "10:15 AM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "10:15 AM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "147", "2", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-1115am",
            name = "11:15 AM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "11:15 AM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "349", "6", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-1215pm",
            name = "12:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "12:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "125", "8", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0115pm",
            name = "01:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "01:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "478", "9", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0215pm",
            name = "02:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "02:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "370", "0", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0315pm",
            name = "03:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "03:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "139", "3", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0415pm",
            name = "04:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "04:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "269", "7", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0515pm",
            name = "05:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "05:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "458", "7", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0615pm",
            name = "06:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "06:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "178", "6", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0715pm",
            name = "07:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "07:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "259", "6", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0815pm",
            name = "08:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "08:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "489", "1", "---")
            )
        ),
        MarketItem(
            id = "dhanratan-starline-0915pm",
            name = "09:15 PM",
            category = "starline",
            resultCode = "***-*",
            status = "Betting is Running",
            openTime = "09:15 PM",
            closeTime = "07/10/2026",
            isLive = true,
            multiplierLabel = "10 KA 1600",
            chartHistory = listOf(
                ChartEntry("06/10/2026", "Tue", "567", "8", "---")
            )
        )
    )

    val STARLINE_RESULTS_BY_DATE: Map<String, List<StarlineDateSlot>> = linkedMapOf(
        "07/10/2026" to listOf(
            StarlineDateSlot("10:15 AM", "***-*"),
            StarlineDateSlot("11:15 AM", "***-*"),
            StarlineDateSlot("12:15 PM", "***-*"),
            StarlineDateSlot("01:15 PM", "***-*"),
            StarlineDateSlot("02:15 PM", "***-*"),
            StarlineDateSlot("03:15 PM", "***-*"),
            StarlineDateSlot("04:15 PM", "***-*"),
            StarlineDateSlot("05:15 PM", "***-*"),
            StarlineDateSlot("06:15 PM", "***-*"),
            StarlineDateSlot("07:15 PM", "***-*"),
            StarlineDateSlot("08:15 PM", "***-*"),
            StarlineDateSlot("09:15 PM", "***-*")
        ),
        "06/10/2026" to listOf(
            StarlineDateSlot("10:15 AM", "147-2"),
            StarlineDateSlot("11:15 AM", "349-6"),
            StarlineDateSlot("12:15 PM", "125-8"),
            StarlineDateSlot("01:15 PM", "478-9"),
            StarlineDateSlot("02:15 PM", "370-0"),
            StarlineDateSlot("03:15 PM", "139-3"),
            StarlineDateSlot("04:15 PM", "269-7"),
            StarlineDateSlot("05:15 PM", "458-7"),
            StarlineDateSlot("06:15 PM", "178-6"),
            StarlineDateSlot("07:15 PM", "259-6"),
            StarlineDateSlot("08:15 PM", "489-1"),
            StarlineDateSlot("09:15 PM", "567-8")
        ),
        "05/10/2026" to listOf(
            StarlineDateSlot("10:15 AM", "236-1"),
            StarlineDateSlot("11:15 AM", "159-5"),
            StarlineDateSlot("12:15 PM", "369-8"),
            StarlineDateSlot("01:15 PM", "248-4"),
            StarlineDateSlot("02:15 PM", "578-0"),
            StarlineDateSlot("03:15 PM", "128-1"),
            StarlineDateSlot("04:15 PM", "359-7"),
            StarlineDateSlot("05:15 PM", "270-9"),
            StarlineDateSlot("06:15 PM", "140-5"),
            StarlineDateSlot("07:15 PM", "370-0"),
            StarlineDateSlot("08:15 PM", "125-8"),
            StarlineDateSlot("09:15 PM", "240-6")
        ),
        "04/10/2026" to listOf(
            StarlineDateSlot("10:15 AM", "129-2"),
            StarlineDateSlot("11:15 AM", "258-5"),
            StarlineDateSlot("12:15 PM", "469-9"),
            StarlineDateSlot("01:15 PM", "136-0"),
            StarlineDateSlot("02:15 PM", "280-0"),
            StarlineDateSlot("03:15 PM", "479-0"),
            StarlineDateSlot("04:15 PM", "569-0"),
            StarlineDateSlot("05:15 PM", "146-1"),
            StarlineDateSlot("06:15 PM", "245-1"),
            StarlineDateSlot("07:15 PM", "380-1"),
            StarlineDateSlot("08:15 PM", "560-1"),
            StarlineDateSlot("09:15 PM", "678-1")
        )
    )

    val MAIN_GAME_RATES: List<GameRateItem> = listOf(
        GameRateItem("SINGLE DIGIT :", "10 KA 95", 9.5),
        GameRateItem("JODI DIGIT :", "10 KA 950", 95.0),
        GameRateItem("SINGLE PANNA :", "10 KA 1400", 140.0),
        GameRateItem("DOUBLE PANNA :", "10 KA 2800", 280.0),
        GameRateItem("TRIPLE PANNA :", "10 KA 8000", 800.0),
        GameRateItem("HALF SANGAM :", "10 KA 10000", 1000.0),
        GameRateItem("FULL SANGAM :", "10 KA 100000", 10000.0)
    )

    val STARLINE_GAME_RATES: List<GameRateItem> = listOf(
        GameRateItem("SINGLE DIGIT :", "10 KA 100", 10.0),
        GameRateItem("SINGLE PANNA :", "10 KA 1600", 160.0),
        GameRateItem("DOUBLE PANNA :", "10 KA 3000", 300.0),
        GameRateItem("TRIPLE PANNA :", "10 KA 10000", 1000.0)
    )

    val SINGLE_PANA_BY_ANK: Map<String, List<String>> = mapOf(
        "0" to listOf("127", "136", "145", "190", "235", "280", "370", "389", "460", "479", "569", "578"),
        "1" to listOf("128", "137", "146", "236", "245", "290", "380", "470", "489", "560", "579", "678"),
        "2" to listOf("129", "138", "147", "156", "237", "246", "345", "390", "480", "570", "589", "679"),
        "3" to listOf("120", "139", "148", "157", "238", "247", "256", "346", "490", "580", "670", "689"),
        "4" to listOf("130", "149", "158", "167", "239", "248", "257", "347", "356", "590", "680", "789"),
        "5" to listOf("140", "159", "168", "230", "249", "258", "267", "348", "357", "456", "690", "780"),
        "6" to listOf("123", "150", "169", "178", "240", "259", "268", "349", "358", "367", "457", "790"),
        "7" to listOf("124", "160", "179", "250", "269", "278", "340", "359", "368", "458", "467", "890"),
        "8" to listOf("125", "134", "170", "189", "260", "279", "350", "369", "378", "459", "468", "567"),
        "9" to listOf("126", "135", "180", "234", "270", "289", "360", "379", "450", "469", "478", "568")
    )

    val DOUBLE_PANA_BY_ANK: Map<String, List<String>> = mapOf(
        "0" to listOf("118", "226", "244", "299", "334", "488", "550", "668", "677"),
        "1" to listOf("100", "119", "155", "227", "335", "344", "399", "588", "669"),
        "2" to listOf("110", "200", "228", "255", "336", "499", "660", "688", "778"),
        "3" to listOf("166", "229", "300", "337", "355", "445", "599", "779", "788"),
        "4" to listOf("112", "220", "266", "338", "400", "446", "455", "699", "770"),
        "5" to listOf("113", "122", "177", "339", "366", "447", "500", "799", "889"),
        "6" to listOf("114", "277", "330", "448", "466", "556", "600", "880", "899"),
        "7" to listOf("115", "133", "188", "223", "377", "449", "557", "566", "700"),
        "8" to listOf("116", "224", "233", "288", "440", "477", "558", "800", "990"),
        "9" to listOf("117", "144", "199", "225", "388", "559", "577", "667", "900")
    )

    /**
     * Double Patti must be a 3-digit number where exactly 2 digits repeat (e.g., 112, 220, 448, 121).
     */
    fun isValidDoublePatti(digits: String): Boolean {
        if (digits.length != 3 || !digits.all { it.isDigit() }) return false
        val counts = digits.groupingBy { it }.eachCount()
        return counts.size == 2 && counts.values.contains(2)
    }

    /**
     * Single Patti must be a 3-digit number with 3 distinct digits (no repeating digits).
     */
    fun isValidSinglePatti(digits: String): Boolean {
        if (digits.length != 3 || !digits.all { it.isDigit() }) return false
        return digits.toSet().size == 3
    }

    /**
     * Triple Patti must be a 3-digit number where all 3 digits are identical (e.g., 000, 111, 777).
     */
    fun isValidTriplePatti(digits: String): Boolean {
        if (digits.length != 3 || !digits.all { it.isDigit() }) return false
        return digits.toSet().size == 1
    }

    /**
     * Calculates the single Ank (0-9) from a 3-digit Pana by summing its digits modulo 10.
     */
    fun calculateAnkFromPana(pana: String): String {
        if (pana.length != 3 || !pana.all { it.isDigit() }) return "*"
        val sum = pana.sumOf { it.digitToInt() }
        return (sum % 10).toString()
    }

    /**
     * Parses time strings like "10:15 AM", "02.05 PM", "9:55 PM", "12:40 PM" into minutes since midnight (0..1439).
     */
    fun parseTimeMinutesOfDay(timeStr: String): Int? {
        val cleaned = timeStr.trim().replace('.', ':').uppercase()
        val regex = Regex("""^(\d{1,2}):(\d{2})\s*(AM|PM)$""")
        val match = regex.find(cleaned) ?: return null
        val rawHour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull() ?: return null
        val amPm = match.groupValues[3]
        if (rawHour !in 1..12 || minute !in 0..59) return null

        val hour24 = when {
            amPm == "AM" && rawHour == 12 -> 0
            amPm == "AM" -> rawHour
            amPm == "PM" && rawHour == 12 -> 12
            else -> rawHour + 12
        }
        return hour24 * 60 + minute
    }

    fun currentMinutesOfDay(): Int {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + cal.get(java.util.Calendar.MINUTE)
    }

    /**
     * Returns true if Open session bidding is currently allowed for [market]:
     * - Market is ON (isLive == true)
     * - Open result has not been declared yet
     * - Current time is before Bid Open Time (or Admin explicitly turned the market ON via Admin Panel)
     */
    fun isOpenBidWindowOpen(market: MarketItem, nowMinutes: Int = currentMinutesOfDay()): Boolean {
        if (!market.isLive) return false
        if (market.category == "starline") {
            if (!market.resultCode.contains("*")) return false
            if (market.status.contains("Admin ON", ignoreCase = true)) return true
            val openMins = parseTimeMinutesOfDay(market.openTime)
            return openMins == null || nowMinutes < openMins
        }
        val parts = market.resultCode.split("-")
        val openPana = parts.getOrNull(0).orEmpty()
        if (openPana.length == 3 && !openPana.contains("*")) return false
        if (market.status.contains("Admin ON", ignoreCase = true)) return true
        val openMins = parseTimeMinutesOfDay(market.openTime)
        return openMins == null || nowMinutes < openMins
    }

    /**
     * Returns true if Close session bidding is currently allowed for [market]:
     * - Market is ON (isLive == true)
     * - Market is a Main market (Starline only has single result)
     * - Close result has not been declared yet
     * - Current time is before Bid Close Time (or Admin explicitly turned the market ON via Admin Panel)
     */
    fun isCloseBidWindowOpen(market: MarketItem, nowMinutes: Int = currentMinutesOfDay()): Boolean {
        if (!market.isLive) return false
        if (market.category == "starline") return false
        val parts = market.resultCode.split("-")
        val closePana = parts.getOrNull(2).orEmpty()
        if (closePana.length == 3 && !closePana.contains("*")) return false
        if (market.status.contains("Admin ON", ignoreCase = true)) return true
        val closeMins = parseTimeMinutesOfDay(market.closeTime)
        return closeMins == null || nowMinutes < closeMins
    }

    fun getMultiplier(game: GameVariety, isStarline: Boolean): Double {
        if (isStarline) {
            return when (game) {
                GameVariety.SINGLE_ANK, GameVariety.SINGLE_DIGIT_BULK -> 10.0
                GameVariety.SINGLE_PATTI, GameVariety.SINGLE_PANA_BULK -> 160.0
                GameVariety.DOUBLE_PATTI, GameVariety.DOUBLE_PANA_BULK -> 300.0
                GameVariety.TRIPLE_PATTI -> 1000.0
                else -> 10.0
            }
        }
        return when (game) {
            GameVariety.SINGLE_ANK, GameVariety.SINGLE_DIGIT_BULK -> 9.5
            GameVariety.JODI, GameVariety.JODI_DIGIT_BULK -> 95.0
            GameVariety.SINGLE_PATTI, GameVariety.SINGLE_PANA_BULK -> 140.0
            GameVariety.DOUBLE_PATTI, GameVariety.DOUBLE_PANA_BULK -> 280.0
            GameVariety.TRIPLE_PATTI -> 800.0
            GameVariety.HALF_SANGAM_A, GameVariety.HALF_SANGAM_B -> 1000.0
            GameVariety.FULL_SANGAM -> 10000.0
        }
    }

    fun buildInitialSupportMessages(userPhone: String): List<SupportMessageEntity> {
        return listOf(
            SupportMessageEntity(
                id = "msg-1-$userPhone",
                userPhone = userPhone,
                sender = "support",
                text = "Namaste! Welcome to DhanRatan Games Support. Select a topic below or type your question regarding Bids, Results, Deposits, Withdrawals, or Game Rules.",
                timestamp = "Online",
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }
}
