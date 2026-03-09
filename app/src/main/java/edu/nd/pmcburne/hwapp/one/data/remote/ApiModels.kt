package edu.nd.pmcburne.hwapp.one.data.remote

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("games") val games: List<Game>? = null
) {
    fun allEvents(): List<Game> = games ?: emptyList()
}

data class Game(
    @SerializedName("gameID") val gameID: String,
    @SerializedName("away") val away: TeamData?,
    @SerializedName("home") val home: TeamData?,
    @SerializedName("gameState") val gameState: String?,
    @SerializedName("currentPeriod") val currentPeriod: String?,
    @SerializedName("contestClock") val contestClock: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("startTime") val startTime: String?,
    @SerializedName("bracketRound") val bracketRound: String?
)

data class TeamData(
    @SerializedName("score") val score: String?,
    @SerializedName("names") val names: TeamNames?,
    @SerializedName("winner") val winner: Boolean?
)

data class TeamNames(
    @SerializedName("char6") val char6: String?,
    @SerializedName("short") val short: String?,
    @SerializedName("full") val full: String?
)