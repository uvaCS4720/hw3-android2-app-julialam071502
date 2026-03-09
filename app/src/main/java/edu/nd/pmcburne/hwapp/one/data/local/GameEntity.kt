package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val eventId: String?,
    val gender: String,
    val gameDate: String,
    val awayTeamName: String,
    val homeTeamName: String,
    val awayScore: String?,
    val homeScore: String?,
    val gameState: String,
    val displayClock: String?,
    val period: Int?,
    val statusDetail: String?,
    val awayWinner: Boolean,
    val homeWinner: Boolean,
    val startTime: String?
)