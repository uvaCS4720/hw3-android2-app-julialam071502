package edu.nd.pmcburne.hwapp.one.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.data.local.GameDao
import edu.nd.pmcburne.hwapp.one.data.local.GameEntity
import edu.nd.pmcburne.hwapp.one.data.remote.BasketballApiService
import edu.nd.pmcburne.hwapp.one.data.remote.Game
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BasketballRepository(
    private val apiService: BasketballApiService,
    private val gameDao: GameDao,
    private val context: Context
) {
    fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun getCachedGames(gender: String, date: LocalDate): Flow<List<GameEntity>> {
        val datePrefix = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return gameDao.getGamesByGenderAndDate(gender, "$datePrefix%")
    }

    suspend fun refreshScoreboard(gender: String, date: LocalDate) {
        val year  = date.format(DateTimeFormatter.ofPattern("yyyy"))
        val month = date.format(DateTimeFormatter.ofPattern("MM"))
        val day   = date.format(DateTimeFormatter.ofPattern("dd"))
        val response = apiService.getScoreboard(gender, year, month, day)
        val entities = response.allEvents().map { mapGameToEntity(it, gender, date) }
        gameDao.upsertGames(entities)
    }

    private fun mapGameToEntity(game: Game, gender: String, date: LocalDate): GameEntity {
        val awayName = game.away?.names?.full
            ?: game.away?.names?.short
            ?: game.away?.names?.char6
            ?: "Unknown"
        val homeName = game.home?.names?.full
            ?: game.home?.names?.short
            ?: game.home?.names?.char6
            ?: "Unknown"

        val gameState = when (game.gameState?.lowercase()) {
            "final" -> "post"
            "live"  -> "in"
            else    -> "pre"
        }

        val gameDateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val statusDetail = when (gameState) {
            "post" -> "Final"
            "in"   -> {
                val period = game.currentPeriod ?: ""
                val clock  = game.contestClock ?: ""
                val label  = if (gender == "women") {
                    when {
                        period.contains("1", true) -> "1st Qtr"
                        period.contains("2", true) -> "2nd Qtr"
                        period.contains("3", true) -> "3rd Qtr"
                        period.contains("4", true) -> "4th Qtr"
                        else -> period
                    }
                } else {
                    when {
                        period.contains("1", true) -> "1st Half"
                        period.contains("2", true) -> "2nd Half"
                        else -> period
                    }
                }
                if (clock.isNotEmpty()) "$clock - $label" else label
            }
            else -> game.startTime ?: "TBD"
        }

        val safeId = game.gameID ?: "${awayName}_${homeName}_$gameDateStr"

        return GameEntity(
            id           = "${safeId}_$gender",
            eventId      = safeId,
            gender       = gender,
            gameDate     = gameDateStr,
            awayTeamName = awayName,
            homeTeamName = homeName,
            awayScore    = game.away?.score,
            homeScore    = game.home?.score,
            gameState    = gameState,
            displayClock = game.contestClock,
            period       = null,
            statusDetail = statusDetail,
            awayWinner   = game.away?.winner ?: false,
            homeWinner   = game.home?.winner ?: false,
            startTime    = game.startTime
        )
    }
}