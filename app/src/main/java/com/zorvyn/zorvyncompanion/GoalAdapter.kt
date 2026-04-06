package com.zorvyn.zorvyncompanion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zorvyn.zorvyncompanion.databinding.GoalItemBinding
import java.text.NumberFormat
import java.util.Locale

class GoalAdapter(
    private var goals: List<SavingGoal>,
    private val onContribution: (String, Double) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    fun updateData(newGoals: List<SavingGoal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = GoalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount() = goals.size

    inner class GoalViewHolder(private val binding: GoalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: SavingGoal) {
            binding.tvGoalName.text = goal.name
            
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            binding.tvGoalStatus.text = "${formatter.format(goal.current)} of ${formatter.format(goal.target)}"
            
            val progress = ((goal.current / goal.target) * 100).toInt().coerceIn(0, 100)
            binding.tvGoalPercent.text = "$progress%"
            binding.goalProgress.progress = progress
            
            binding.btn25.setOnClickListener { onContribution(goal.id, 25.0) }
            binding.btn50.setOnClickListener { onContribution(goal.id, 50.0) }
            binding.btn100.setOnClickListener { onContribution(goal.id, 100.0) }
            binding.btnDelete.setOnClickListener { onDelete(goal.id) }
        }
    }
}
