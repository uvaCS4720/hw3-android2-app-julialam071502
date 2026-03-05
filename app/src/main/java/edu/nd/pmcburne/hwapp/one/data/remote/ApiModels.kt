package edu.nd.pmcburne.hwapp.one.data.remote

import com.google.gson.annotations.SerializedName

data class ScoreboardResponse(
    @SerializedName("events") val events: List<Event>? = null,
    @SerializedName("games") val games: List<Event>? = null
) {
    fun allEvents(): List<Event> = (events ?: emptyList()) + (games ?: emptyList())
}

data class Event(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("competitions") val competitions: List<Competition>?,
    @SerializedName("status") val status: EventStatus?
)

data class Competition(
    @SerializedName("id") val id: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("competitors") val competitors: List<Competitor>?,
    @SerializedName("status") val status: CompetitionStatus?
)

data class Competitor(
    @SerializedName("homeAway") val homeAway: String?,
    @SerializedName("winner") val winner: Boolean?,
    @SerializedName("team") val team: Team?,
    @SerializedName("score") val score: String?
)

data class Team(
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("shortDisplayName") val shortDisplayName: String?,
    @SerializedName("location") val location: String?
)

data class CompetitionStatus(
    @SerializedName("displayClock") val displayClock: String?,
    @SerializedName("period") val period: Int?,
    @SerializedName("type") val type: StatusType?
)

data class EventStatus(
    @SerializedName("displayClock") val displayClock: String?,
    @SerializedName("period") val period: Int?,
    @SerializedName("type") val type: StatusType?
)

data class StatusType(
    @SerializedName("state") val state: String?,
    @SerializedName("completed") val completed: Boolean?,
    @SerializedName("detail") val detail: String?,
    @SerializedName("shortDetail") val shortDetail: String?
)