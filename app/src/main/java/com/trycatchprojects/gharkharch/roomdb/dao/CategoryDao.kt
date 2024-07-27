package com.trycatchprojects.gharkharch.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trycatchprojects.gharkharch.roomdb.entities.CategoryEntity

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM category")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT id FROM category WHERE name = :name LIMIT 1")
    suspend fun getCategoryIdByName(name: String): Int?

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

}