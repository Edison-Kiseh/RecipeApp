package com.example.myrecipeapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class RecipeTable (
    @PrimaryKey(autoGenerate = true)
    var recipeId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "prep_time")
    var prepTime: String,

    @ColumnInfo(name = "image")
    var image: String,

    @ColumnInfo(name = "stepCount")
    var stepCount: Int,

    @ColumnInfo(name = "ingredientCount")
    var ingredientCount: Int,
)
