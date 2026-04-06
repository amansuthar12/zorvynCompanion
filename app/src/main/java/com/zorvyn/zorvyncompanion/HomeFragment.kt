package com.zorvyn.zorvyncompanion

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.zorvyn.zorvyncompanion.databinding.FragmentHomeBinding
import com.google.android.material.transition.MaterialSharedAxis
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val updateListener = { updateUI() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 300
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 300
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()
        setupListeners()
        TransactionManager.subscribe(updateListener)
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.fabAdd to "add_transaction_fab")
            findNavController().navigate(
                R.id.action_homeFragment_to_addTransactionFragment,
                null,
                null,
                extras
            )
        }
        
        binding.balanceCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transactionsFragment)
        }
    }

    private fun updateUI() {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        val balance = TransactionManager.getBalance()
        binding.tvBalanceAmount.text = if (balance >= 0) {
            formatter.format(balance)
        } else {
            "-${formatter.format(-balance)}"
        }
        
        binding.tvHomeIncome.text = "Income: ${formatter.format(TransactionManager.getIncome())}"
        binding.tvHomeExpense.text = "Expense: ${formatter.format(TransactionManager.getExpense())}"
        
        setupPieChart() 
    }

    private fun setupCharts() {
        setupBarChart()
        setupPieChart()
    }

    private fun setupBarChart() {
        val barChart: BarChart = binding.barChart
        val days = arrayOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
        
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 15f))
        entries.add(BarEntry(1f, 10f))
        entries.add(BarEntry(2f, 25f))
        entries.add(BarEntry(3f, 12f))
        entries.add(BarEntry(4f, 18f))
        entries.add(BarEntry(5f, 8f))
        entries.add(BarEntry(6f, 14f))

        val colors = ArrayList<Int>()
        for (i in 0 until entries.size) {
            if (i == 2) colors.add(Color.parseColor("#4CAF50"))
            else colors.add(Color.parseColor("#EF5350"))
        }

        val dataSet = BarDataSet(entries, "Daily Activity")
        dataSet.colors = colors
        dataSet.setDrawValues(false)

        val data = BarData(dataSet)
        data.barWidth = 0.5f

        barChart.data = data
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setTouchEnabled(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(days)
        xAxis.granularity = 1f
        xAxis.textColor = Color.GRAY

        barChart.axisLeft.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textColor = Color.GRAY
        }
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000, Easing.EaseInOutQuad)
        barChart.invalidate()
    }

    private fun setupPieChart() {
        val pieChart: PieChart = binding.pieChart
        
        val income = TransactionManager.getIncome().toFloat()
        val expense = TransactionManager.getExpense().toFloat()
        
        val entries = ArrayList<PieEntry>()
        if (income > 0 || expense > 0) {
            entries.add(PieEntry(income, "Income"))
            entries.add(PieEntry(expense, "Expense"))
        } else {
            entries.add(PieEntry(1f, "No Data"))
        }

        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#4CAF50"))
        colors.add(Color.parseColor("#EF5350"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 3f

        val data = PieData(dataSet)
        pieChart.data = data
        
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.holeRadius = 75f
        pieChart.transparentCircleRadius = 0f
        
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.setTouchEnabled(false)
        
        pieChart.animateY(1000, Easing.EaseInOutQuad)
        pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        TransactionManager.unsubscribe(updateListener)
        _binding = null
    }
}
