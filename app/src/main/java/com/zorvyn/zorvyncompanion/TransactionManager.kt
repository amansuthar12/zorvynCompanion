package com.zorvyn.zorvyncompanion

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TransactionManager {
    private const val PREFS_NAME = "zorvyn_prefs"
    private const val KEY_TRANSACTIONS = "transactions"
    
    private val transactions = mutableListOf<Transaction>()
    private val listeners = mutableListOf<() -> Unit>()
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadTransactions()
    }

    private fun loadTransactions() {
        val json = prefs.getString(KEY_TRANSACTIONS, null)
        if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            val savedList: List<Transaction> = gson.fromJson(json, type)
            transactions.clear()
            transactions.addAll(savedList)
        } else {
            // Seed with sample data if first run
            transactions.addAll(listOf(
                Transaction(amount = 25000.0, category = "Salary", date = "Jan 01, 2026", type = TransactionType.INCOME),
                Transaction(amount = 120.50, category = "Grocery", date = "Jan 02, 2026", type = TransactionType.EXPENSE),
                Transaction(amount = 45.00, category = "Transport", date = "Jan 02, 2026", type = TransactionType.EXPENSE),
                Transaction(amount = 800.00, category = "Investments", date = "Jan 03, 2026", type = TransactionType.INCOME)
            ))
            saveTransactions()
        }
    }

    private fun saveTransactions() {
        if (::prefs.isInitialized) {
            val json = gson.toJson(transactions)
            prefs.edit().putString(KEY_TRANSACTIONS, json).apply()
        }
    }

    fun subscribe(listener: () -> Unit) {
        listeners.add(listener)
        listener() 
    }

    fun unsubscribe(listener: () -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyUpdate() {
        listeners.forEach { it() }
    }

    fun getAll() = transactions.toList()

    fun addTransaction(transaction: Transaction) {
        transactions.add(0, transaction)
        saveTransactions()
        notifyUpdate()
    }

    fun updateTransaction(transaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = transaction
            saveTransactions()
            notifyUpdate()
        }
    }

    fun deleteTransaction(id: String) {
        transactions.removeAll { it.id == id }
        saveTransactions()
        notifyUpdate()
    }

    fun getIncome(): Double {
        return transactions.filter { it.type == TransactionType.INCOME }
            .map { it.absoluteAmount }
            .sum()
    }

    fun getExpense(): Double {
        return transactions.filter { it.type == TransactionType.EXPENSE }
            .map { it.absoluteAmount }
            .sum()
    }

    fun getBalance(): Double {
        return getIncome() - getExpense()
    }

    fun getCategoryTotals(): Map<String, Double> {
        return transactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    fun getWeeklySpending(): Double {
        // For simplicity in this demo, we'll sum expenses from the last 7 entries
        return transactions.filter { it.type == TransactionType.EXPENSE }
            .take(7)
            .sumOf { it.amount }
    }
}
