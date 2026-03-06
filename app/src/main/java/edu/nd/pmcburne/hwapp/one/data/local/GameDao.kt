package edu.nd.pmcburne.hwapp.one.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE gender = :gender AND gameDate LIKE :datePrefix ORDER BY startTime ASC")
    fun getGamesByGenderAndDate(gender: String, datePrefix: String): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(games: List<GameEntity>)
}