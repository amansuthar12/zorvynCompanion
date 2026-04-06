package com.zorvyn.zorvyncompanion

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class SavingGoal(
    val id: String,
    val name: String,
    val target: Double,
    var current: Double = 0.0
)

object GoalManager {
    private const val PREFS_NAME = "goal_prefs_v2"
    private const val KEY_GOALS = "goals_list"
    private const val KEY_DAILY_BUDGET = "daily_budget"
    
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    
    private val goals = mutableListOf<SavingGoal>()
    var dailyBudget: Double = 50.0
        private set

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadData()
    }

    private fun loadData() {
        val goalsJson = prefs.getString(KEY_GOALS, null)
        if (goalsJson != null) {
            val type = object : TypeToken<List<SavingGoal>>() {}.type
            val savedGoals: List<SavingGoal> = gson.fromJson(goalsJson, type)
            goals.clear()
            goals.addAll(savedGoals)
        } else {
            // Default goal
            goals.add(SavingGoal(System.currentTimeMillis().toString(), "Emergency Fund", 5000.0, 2350.0))
            saveData()
        }
        dailyBudget = prefs.getFloat(KEY_DAILY_BUDGET, 50.0f).toDouble()
    }

    private fun saveData() {
        if (::prefs.isInitialized) {
            prefs.edit()
                .putString(KEY_GOALS, gson.toJson(goals))
                .putFloat(KEY_DAILY_BUDGET, dailyBudget.toFloat())
                .apply()
        }
    }

    fun getAllGoals(): List<SavingGoal> = goals

    fun addGoal(name: String, target: Double) {
        goals.add(SavingGoal(System.currentTimeMillis().toString(), name, target))
        saveData()
    }

    fun deleteGoal(id: String) {
        goals.removeIf { it.id == id }
        saveData()
    }

    fun addContribution(id: String, amount: Double) {
        goals.find { it.id == id }?.let {
            it.current += amount
            saveData()
        }
    }

    fun updateDailyBudget(budget: Double) {
        dailyBudget = budget
        saveData()
    }

    fun getStreak(): Int {
        // Mock streak for now
        return 5
    }

    fun getBestStreak(): Int {
        return 12
    }
}
