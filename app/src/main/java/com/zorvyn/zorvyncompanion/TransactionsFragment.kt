package com.zorvyn.zorvyncompanion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zorvyn.zorvyncompanion.databinding.FragmentTransactionsBinding
import com.google.android.material.transition.MaterialSharedAxis

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 300
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 300
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        updateList()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter(
            onTransactionClick = { transaction ->
                val bundle = Bundle().apply { putSerializable("transaction", transaction) }
                findNavController().navigate(R.id.action_transactionsFragment_to_addTransactionFragment, bundle)
            },
            onTransactionLongClick = { transaction ->
                showDeleteConfirmation(transaction)
            }
        )
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        
        binding.fabAdd.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.fabAdd to "add_transaction_fab")
            findNavController().navigate(
                R.id.action_transactionsFragment_to_addTransactionFragment,
                null, 
                null,
                extras
            )
        }

        binding.toggleFilter.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                updateList()
            }
        }
    }

    private fun updateList() {
        val allTransactions = TransactionManager.getAll()
        val filteredList = when (binding.toggleFilter.checkedButtonId) {
            R.id.btnIncome -> allTransactions.filter { it.type == TransactionType.INCOME }
            R.id.btnExpense -> allTransactions.filter { it.type == TransactionType.EXPENSE }
            else -> allTransactions
        }
        adapter.submitList(filteredList)
    }

    private fun showDeleteConfirmation(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                TransactionManager.deleteTransaction(transaction.id)
                updateList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
