package com.trycatchprojects.gharkharch.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trycatchprojects.gharkharch.roomdb.entities.IncomeEntity

@Dao
interface IncomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Query("SELECT * FROM income")
    suspend fun getAllIncomes(): List<IncomeEntity>

    @Query("SELECT SUM(amount) FROM income")
    suspend fun getTotalIncome(): Double?

    @Query("SELECT * FROM income WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getIncomesByDateRange(startDate: Long, endDate: Long): List<IncomeEntity>

    @Delete
    suspend fun delete(income: IncomeEntity)


}