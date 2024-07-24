package com.trycatchprojects.gharkharch.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expense")
    suspend fun getAllExpenses(): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expense")
    suspend fun getTotalExpense(): Double?
}