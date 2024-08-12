package com.example.myrecipeapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StepDao {
    @Insert
    fun insertStep(step: StepsTable)

    @Query("SELECT * FROM steps_table")
    suspend fun getAllSteps(): List<StepsTable>

    @Query("SELECT name FROM steps_table WHERE recipe_id = :recipeId")
    fun getSteps(recipeId: Long): List<String>

    @Query("DELETE FROM steps_table WHERE recipe_id = :recipeId")
    fun deleteSteps(recipeId: Long)

    @Query("SELECT COUNT(*) FROM steps_table WHERE recipe_id = :recipeId")
    fun getNumberOfSteps(recipeId: Long) : Int
}