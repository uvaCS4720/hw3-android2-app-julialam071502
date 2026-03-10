package edu.nd.pmcburne.hwapp.one.data.remote

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("games") val games: List<GameWrapper>? = null
) {
    fun allEvents(): List<Game> = games?.mapNotNull { it.game } ?: emptyList()
}
data class GameWrapper(
    @SerializedName("game") val game: Game?
)

data class Game(
    @SerializedName("gameID") val gameID: String?,
    @SerializedName("away") val away: TeamData?,
    @SerializedName("home") val home: TeamData?,
    @SerializedName("gameState") val gameState: String?,
    @SerializedName("currentPeriod") val currentPeriod: String?,
    @SerializedName("contestClock") val contestClock: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("title") val title: String?
)

data class TeamData(
    @SerializedName("score") val score: String?,
    @SerializedName("winner") val winner: Boolean?,
    @SerializedName("names") val names: TeamNames?
)

data class TeamNames(
    @SerializedName("char6") val char6: String?,
    @SerializedName("short") val short: String?,
    @SerializedName("full") val full: String?
)