package com.trycatchprojects.gharkharch.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trycatchprojects.gharkharch.roomdb.dao.CategoryDao
import com.trycatchprojects.gharkharch.roomdb.dao.ExpenseDao
import com.trycatchprojects.gharkharch.roomdb.dao.IncomeDao
import com.trycatchprojects.gharkharch.roomdb.entities.CategoryEntity
import com.trycatchprojects.gharkharch.roomdb.entities.ExpenseEntity
import com.trycatchprojects.gharkharch.roomdb.entities.IncomeEntity


@Database(entities = [IncomeEntity::class,CategoryEntity::class,ExpenseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ghar_kharch_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}