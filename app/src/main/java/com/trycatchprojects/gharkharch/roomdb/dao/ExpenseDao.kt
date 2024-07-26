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

    @Query("SELECT * FROM expense WHERE categoryId = :categoryId")
    suspend fun getExpensesByCategoryId(categoryId: Int): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expense")
    suspend fun getTotalExpense(): Double?

    @Query("SELECT * FROM expense WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpensesByDateRange(startDate: Long, endDate: Long): List<ExpenseEntity>

}