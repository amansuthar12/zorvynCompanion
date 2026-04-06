package com.zorvyn.zorvyncompanion

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.zorvyn.zorvyncompanion.databinding.FragmentInsightsBinding
import java.text.NumberFormat
import java.util.Locale

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        
        // Setup comparison summary
        val thisWeek = TransactionManager.getWeeklySpending()
        binding.tvThisWeek.text = formatter.format(thisWeek)
        binding.tvLastWeek.text = formatter.format(450.60) // Mock last week for now
        
        setupDonutChart()
        setupCategoryList()
    }

    private fun setupDonutChart() {
        val categories = TransactionManager.getCategoryTotals()
        val entries = categories.map { (category, total) -> PieEntry(total.toFloat(), category) }
        
        val dataSet = PieDataSet(entries, "")
        val colors = listOf(
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.chart_blue),
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_bills),
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.chart_yellow),
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_food),
            androidx.core.content.ContextCompat.getColor(requireContext(), R.color.chart_red)
        )
        dataSet.colors = colors
        dataSet.setDrawValues(false)
        
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.holeRadius = 70f
        binding.pieChart.setTransparentCircleAlpha(0)
        binding.pieChart.invalidate()
    }

    private fun setupCategoryList() {
        val totals = TransactionManager.getCategoryTotals()
        val grandTotal = totals.values.sum()
        
        val stats = totals.map { (category, amount) ->
            val percentage = if (grandTotal > 0.0) ((amount / grandTotal) * 100).toInt() else 0
            CategoryStat(
                category = category,
                amount = amount,
                percentage = percentage,
                color = when (category) {
                    "Shopping" -> androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_shopping)
                    "Food" -> androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_food)
                    "Transport" -> androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_transport)
                    "Bills" -> androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cat_bills)
                    else -> Color.parseColor("#607D8B")
                },
                iconRes = R.drawable.ic_insights
            )
        }.sortedByDescending { it.amount }

        binding.rvCategoryStats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoryStats.adapter = CategoryAdapter(stats)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
