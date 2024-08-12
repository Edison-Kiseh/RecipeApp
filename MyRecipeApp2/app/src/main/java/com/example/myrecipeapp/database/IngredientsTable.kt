package com.example.myrecipeapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients_table",
    foreignKeys = [ForeignKey(entity = RecipeTable::class,
        parentColumns = ["recipeId"],
        childColumns = ["recipe_id"],
        onDelete = ForeignKey.CASCADE)]
)
data class IngredientsTable (
    @PrimaryKey(autoGenerate = true)
    var ingredientId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "recipe_id")
    var recipeId: Long
)