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

        // Swipe-to-Delete Implementation
        val swipeHandler = object : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT or androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {
            override fun onMove(rv: androidx.recyclerview.widget.RecyclerView, vh: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder) = false
            
            override fun getSwipeDirs(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
                val position = viewHolder.adapterPosition
                if (adapter.currentList[position] is TransactionListItem.Header) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.currentList[position]
                if (item is TransactionListItem.TransactionItem) {
                    showDeleteConfirmation(item.transaction) {
                        // Reset swipe state if cancelled
                        adapter.notifyItemChanged(position)
                    }
                } else {
                    adapter.notifyItemChanged(position)
                }
            }
        }
        androidx.recyclerview.widget.ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransactions)
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

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                updateList()
                return true
            }
        })
    }

    private fun updateList() {
        val allTransactions = TransactionManager.getAll()
        val query = binding.searchView.query.toString().lowercase()

        val filteredTransactions = allTransactions.filter { transaction ->
            val matchesFilter = when (binding.toggleFilter.checkedButtonId) {
                R.id.btnIncome -> transaction.type == TransactionType.INCOME
                R.id.btnExpense -> transaction.type == TransactionType.EXPENSE
                else -> true
            }

            val matchesSearch = transaction.category.lowercase().contains(query) ||
                    (transaction.notes?.lowercase()?.contains(query) ?: false) ||
                    transaction.amount.toString().contains(query)

            matchesFilter && matchesSearch
        }

        val listItems = mutableListOf<TransactionListItem>()
        val grouped = filteredTransactions.groupBy { it.date }
        
        // Sorting by date (assuming MMM dd, yyyy format, we might need a parser for better sorting)
        grouped.keys.sortedDescending().forEach { date ->
            listItems.add(TransactionListItem.Header(date))
            grouped[date]?.forEach { transaction ->
                listItems.add(TransactionListItem.TransactionItem(transaction))
            }
        }

        adapter.submitList(listItems)
        binding.emptyState.visibility = if (listItems.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showDeleteConfirmation(transaction: Transaction, onCancel: (() -> Unit)? = null) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                TransactionManager.deleteTransaction(transaction.id)
                updateList()
            }
            .setNegativeButton("Cancel") { _, _ ->
                onCancel?.invoke()
            }
            .setOnCancelListener {
                onCancel?.invoke()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
