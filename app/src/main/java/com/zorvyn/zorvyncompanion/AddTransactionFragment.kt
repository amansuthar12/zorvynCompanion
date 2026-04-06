package com.zorvyn.zorvyncompanion

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialContainerTransform
import com.zorvyn.zorvyncompanion.databinding.FragmentAddTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()
    private var existingTransaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.parseColor("#F5F5F5"))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        @Suppress("DEPRECATION")
        existingTransaction = arguments?.getSerializable("transaction") as? Transaction
        
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        
        if (existingTransaction != null) {
            binding.toolbar.title = "Edit Transaction"
            binding.btnSave.text = "Update Transaction"
            fillExistingData(existingTransaction!!)
        } else {
            updateDateLabel()
        }

        val categories = arrayOf("Salary", "Grocery", "Transport", "Food", "Bills", "Shopping", "Entertainment", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
    }

    private fun fillExistingData(t: Transaction) {
        binding.etAmount.setText(t.absoluteAmount.toString())
        binding.actvCategory.setText(t.category, false)
        binding.etDate.setText(t.date)
        binding.etNotes.setText(t.notes ?: "")
        
        if (t.type == TransactionType.INCOME) {
            binding.toggleType.check(R.id.btnIncomeType)
        } else {
            binding.toggleType.check(R.id.btnExpenseType)
        }
    }

    private fun setupListeners() {
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.btnSave.setOnClickListener { saveTransaction() }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                updateDateLabel()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        binding.etDate.setText(sdf.format(calendar.time))
    }

    private fun saveTransaction() {
        val amountStr = binding.etAmount.text.toString()
        val category = binding.actvCategory.text.toString()
        val date = binding.etDate.text.toString()
        val notes = binding.etNotes.text.toString()
        val type = if (binding.toggleType.checkedButtonId == R.id.btnIncomeType) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }

        if (amountStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = Math.abs(amountStr.toDoubleOrNull() ?: 0.0)
        
        if (existingTransaction != null) {
            val updated = existingTransaction!!.copy(
                amount = amount,
                category = category,
                date = date,
                type = type,
                notes = notes
            )
            TransactionManager.updateTransaction(updated)
            Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show()
        } else {
            val newTransaction = Transaction(
                amount = amount,
                category = category,
                date = date,
                type = type,
                notes = notes
            )
            TransactionManager.addTransaction(newTransaction)
            Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show()
        }
        
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
