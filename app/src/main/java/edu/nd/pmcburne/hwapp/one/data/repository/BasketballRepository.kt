package edu.nd.pmcburne.hwapp.one.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.data.local.GameDao
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.remote.BasketballApiService
import edu.nd.pmcburne.hwapp.one.data.remote.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BasketballRepository(
    private val apiService: BasketballApiService,
    private val gameDao: GameDao,
    private val context: Context
) {
    fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getCachedGames(gender: String, date: LocalDate): Flow<List<GameEntity>> {
        val datePrefix = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return gameDao.getGamesByGenderAndDate(gender, "$datePrefix%")
    }

    suspend fun refreshScoreboard(gender: String, date: LocalDate) {
        val year = date.format(DateTimeFormatter.ofPattern("yyyy"))
        val month = date.format(DateTimeFormatter.ofPattern("MM"))
        val day = date.format(DateTimeFormatter.ofPattern("dd"))
        val response = apiService.getScoreboard(gender, year, month, day)
        val entities = response.allEvents().mapNotNull { mapEventToEntity(it, gender, date) }
        gameDao.upsertGames(entities)
    }

    private fun mapEventToEntity(event: Event, gender: String, date: LocalDate): GameEntity? {
        val competition = event.competitions?.firstOrNull() ?: return null
        val competitors = competition.competitors ?: return null

        val away = competitors.firstOrNull { it.homeAway == "away" } ?: competitors.getOrNull(1)
        val home = competitors.firstOrNull { it.homeAway == "home" } ?: competitors.getOrNull(0)

        val awayName = away?.team?.displayName ?: away?.team?.location ?: "Unknown"
        val homeName = home?.team?.displayName ?: home?.team?.location ?: "Unknown"

        val status = competition.status
        val gameState = status?.type?.state ?: "pre"

        val statusDetail = when (gameState) {
            "post" -> "Final"
            "in" -> {
                val period = status?.period
                val clock = status?.displayClock
                val label = if (gender == "women") {
                    when (period) { 1 -> "1st Qtr"; 2 -> "2nd Qtr"; 3 -> "3rd Qtr"; 4 -> "4th Qtr"; else -> "OT" }
                } else {
                    when (period) { 1 -> "1st Half"; 2 -> "2nd Half"; else -> "OT" }
                }
                if (clock != null) "$clock - $label" else label
            }
            else -> formatStartTime(event.date ?: competition.date)
        }

        val gameDateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return GameEntity(
            id = "${event.id}_$gender",
            eventId = event.id,
            gender = gender,
            gameDate = gameDateStr,
            awayTeamName = awayName,
            homeTeamName = homeName,
            awayScore = away?.score,
            homeScore = home?.score,
            gameState = gameState,
            displayClock = status?.displayClock,
            period = status?.period,
            statusDetail = statusDetail,
            awayWinner = away?.winner ?: false,
            homeWinner = home?.winner ?: false,
            startTime = event.date ?: competition.date
        )
    }

    private fun formatStartTime(isoDate: String?): String {
        if (isoDate == null) return "TBD"
        return try {
            val zdt = ZonedDateTime.parse(isoDate)
            val eastern = zdt.withZoneSameInstant(ZoneId.of("America/New_York"))
            eastern.format(DateTimeFormatter.ofPattern("h:mm a 'ET'"))
        } catch (e: Exception) { isoDate }
    }
}