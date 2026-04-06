package com.zorvyn.zorvyncompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zorvyn.zorvyncompanion.databinding.FragmentGoalsBinding
import com.zorvyn.zorvyncompanion.databinding.DialogEditGoalBinding
import java.text.NumberFormat
import java.util.Locale

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GoalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        updateUI()
    }

    private fun setupRecyclerView() {
        adapter = GoalAdapter(
            goals = GoalManager.getAllGoals(),
            onContribution = { id, amount ->
                GoalManager.addContribution(id, amount)
                updateUI()
            },
            onDelete = { id ->
                GoalManager.deleteGoal(id)
                updateUI()
            }
        )
        binding.rvGoals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGoals.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnAddGoal.setOnClickListener {
            showAddGoalDialog()
        }
    }

    private fun showAddGoalDialog() {
        val dialogBinding = DialogEditGoalBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Goal")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etGoalName.text.toString()
                val target = dialogBinding.etGoalTarget.text.toString().toDoubleOrNull() ?: 0.0
                if (name.isNotEmpty() && target > 0) {
                    GoalManager.addGoal(name, target)
                    updateUI()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUI() {
        adapter.updateData(GoalManager.getAllGoals())
        
        binding.tvMainStreakCount.text = "${GoalManager.getStreak()} days"
        binding.tvBestStreakCount.text = "${GoalManager.getBestStreak()} days"
        
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        binding.tvDailyBudget.text = formatter.format(GoalManager.dailyBudget)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
