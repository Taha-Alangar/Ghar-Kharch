package com.trycatchprojects.gharkharch.utils

sealed class Transaction {
    data class Income(val id: Int, val name: String, val amount: Double, val date: String) : Transaction()
    data class Expense(val id: Int, val categoryId: Int,val name:String, val amount: Double, val date: String) : Transaction()
}
