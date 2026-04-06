package com.zorvyn.zorvyncompanion

object TransactionManager {
    private val transactions = mutableListOf<Transaction>(
        Transaction(amount = 25000.0, category = "Salary", date = "Jan 01, 2026", type = TransactionType.INCOME),
        Transaction(amount = 120.50, category = "Grocery", date = "Jan 02, 2026", type = TransactionType.EXPENSE),
        Transaction(amount = 45.00, category = "Transport", date = "Jan 02, 2026", type = TransactionType.EXPENSE),
        Transaction(amount = 800.00, category = "Investments", date = "Jan 03, 2026", type = TransactionType.INCOME)
    )

    private val listeners = mutableListOf<() -> Unit>()

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
        notifyUpdate()
    }

    fun updateTransaction(transaction: Transaction) {
        val index = transactions.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            transactions[index] = transaction
            notifyUpdate()
        }
    }

    fun deleteTransaction(id: String) {
        transactions.removeAll { it.id == id }
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
}
