package com.zorvyn.zorvyncompanion

import java.io.Serializable
import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val category: String,
    val date: String,
    val type: TransactionType,
    val notes: String? = null
) : Serializable {
    val absoluteAmount: Double get() = Math.abs(amount)
}
